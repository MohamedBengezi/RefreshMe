package ca.mcmaster.cas.RefreshMe;

import java.io.FileNotFoundException;

/**
 * This class provides an access routines which calls all of the
 * required methods and algorithms in the correct order to process
 * the data read from the dataset, as outlined in the UML State Machine
 * diagram for this class.
 * 
 * @author bengezim
 */

public class RefreshMeRunner {
	// Final paths to the dataset and filterlist
	private static final String datasetPath = "data/dataout.csv";
	private static final String filterPath  = "data/filter.txt";
	
	/**
	 * This runs all of the necessary access routines in order to get a list of phrases which
	 * contain the most popular words, their frequency, and their example phrases.
	 * @param day The integer number of the day of the month
	 * @param maxWords The maximum number of words to display, and normally is the total number of words displayed.
	 * @return an array of Phrases containing the most popular words in descending order
	 * @throws FileNotFoundException if either the filterlist or dataset are missing from the data folder
	 */
	public static Phrase[] getRefreshList(int day, int maxWords) throws FileNotFoundException {
		// Get Hashed words list
		SeparateChainingHashST hashtable = TestValueReader.getWordData(datasetPath, day);
		
		// Build the phrase list
		Phrase[] popularWords = new Phrase[hashtable.size()];
		int i = 0;
		for (String key : hashtable.keys())
			popularWords[i++] = new Phrase(key, hashtable.get(key));
		
		// Sort popularWords (based on frequency)
		Merge.sort(popularWords);
		
		// Initialize the Search object
		Search.createList(filterPath);
		
		// Filter popularWords
		popularWords = Search.filterList(popularWords, maxWords);
		
		// Finds a sample phrase for 
		for (Phrase word : popularWords)
			word.setExample(TestValueReader.getPhraseExample(datasetPath, word.getString(), day));
		
		return popularWords;
	}
}
