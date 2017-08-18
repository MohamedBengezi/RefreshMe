package ca.mcmaster.cas.RefreshMe;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

/**
 * This class provides the necessary routines for parsing and reading
 * in data from the datasets (acting as our replacement for using SQL
 * and SQLite databases). The dataset we use is massive with over 8 
 * million lines in total, so it only samples a certain number of lines
 * from a random offset of the date index. It also will sequential search
 * for an example usage of a given word, and format the usage to an abridged
 * version centered on the word in focus. 
 * 
 * As the dataset covers the span of a month, the byte address offsets of where
 * each of these months are stored in an date index to help speed up the data
 * retrieval for these dates that are further down in the dataset. These byte
 * offsets are possible since the dataset is read-only and therefore static, and
 * the byte offsets were discovered using the DataFormatter tool vanravec wrote
 * by calling it with the arguments "-i <datasetPath>".
 * 
 * @author vanravec
 * @author gyrokoss
 */

public class TestValueReader {
	// The maximum number of lines to sample
	private static final int MAX_LINES = 1000;
	// The date index of byte offsets
	// PLEASE NOTE: These byte indexes are the appropriate ones for Windows (due to CRLF line endings)
	private static final long[] DATE_INDEX = {0L, 91478245L, 206469568L, 355476134L, 430917051L, 
			509554380L, 619221978L, 718129560L, 826903232L, 922143942L, 1035724854L, 1093904936L, 
			1177003265L, 1260448579L, 1358879834L, 1460044383L, 1562496266L, 1686126157L, 1762089833L, 
			1840938712L, 1944214042L, 2056430758L, 2174142526L, 2290518255L, 2398847052L, 2476629139L, 
			2549363153L, 2733890732L, 3174362768L, 3639585281L, 3785903721L};
	
	/**
	 * Retrieves the words and their frequency from the samples of the dataset,
	 * storing them directly into a SeparateChainingHashST to save memory and
	 * skip the step of doing this later.
	 * @param inFile the path of the dataset
	 * @param day the day of the month to scan
	 * @return the Hashtable of the words and their frequencies
	 * @throws IndexOutOfBoundsException if the day given is greater than 30 (number of days in the dataset's month)
	 */
	public static SeparateChainingHashST getWordData(String inFile, int day) throws IndexOutOfBoundsException {
		SeparateChainingHashST hashtable = new SeparateChainingHashST();
		// Random offset from the date start to vary the data read
		Random rand = new Random();
		int offset = rand.nextInt(10000000);
		// Variable to count the number of lines read in
		int numLines = 0;
		
		// Open the file to read
		try (RandomAccessFile raf = new RandomAccessFile(inFile, "r")) {
			// jump to the byte offset of the current day + the random offset
			raf.seek(DATE_INDEX[day-1] + offset);
			raf.readLine(); // bring to next line so we don't start in the middle of a line
			
			// read in the lines
			for (String line; (line = raf.readLine()) != null && numLines < MAX_LINES; ) {
				if (!line.equals("")) {
					String[] entry = line.split(",");
					// Hash all of the words in the line
					for (String word : entry[1].split(" ")) {
						if (!word.equals("")) hashtable.put(word.toLowerCase());
					}
					numLines++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return hashtable;
	}
	
	/**
	 * Does a sequential search for entries that contain a specified word. It then
	 * abridges the quote to a shorter form. Note that this method does a sequential 
	 * search through the etnire database, so calling it with an obscure word may take
	 * a very, very long time.
	 * @param inFile the dataset to parse
	 * @param phrase the word to find an example of
	 * @param day the day to take the sample from
	 * @return the abridged quote
	 */
	public static String getPhraseExample(String inFile, String phrase, int day) {
		// The max number of words on each side of the phrase 
		int phraseSpan = 8;
		String ret = "";
		
		// Open the file to read
		try (RandomAccessFile raf = new RandomAccessFile(inFile, "r")) {
			// jump to the byte offset of the current day
			raf.seek(DATE_INDEX[day-1]);
			
			// read in the lines
			for (String line; (line = raf.readLine()) != null; ) {
				if (!line.equals("")) {
					String[] entry = line.split(",")[1].split(" ");
					
					// See if the current line contains the string
					for (int i = 0; i < entry.length; i++) {
						if (entry[i].toLowerCase().equals(phrase)) {
							// Format line to compressed format
							int start = 0, end = entry.length;
							if (i - phraseSpan > 0)	{
								start = i - phraseSpan;
								ret += "...";
							}
							if (i + phraseSpan < entry.length) end = i + phraseSpan;
							for (int j = start; j < end - 1; j++)
								ret += entry[j] + " ";
							raf.close();
							// Return the abridged string
							return ret += entry[end-1] + "...";
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Return null if nothing is found
		return null; 
	}
}
