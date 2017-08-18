package ca.mcmaster.cas.RefreshMe;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class provides the routines for the Search functions to filter out
 * the common words from the filterlist. This class was initially written by
 * shanksl and bengezm entirely, but vanravec went to optimize and change up
 * their implementation into what it is now.
 * 
 * @author bengezim
 * @author shanksl
 * @author vanravec
 */

public class Search {
    // BST for storing the filterlist
	private static BST<String, Integer> bst = new BST<String, Integer>();
	
	/**
	 * Reads in the filterlist and loads into a Binary Search Tree.
	 * This method is assumed to be called before any other method in this class is called.
	 * @param fileName The path of the filterlist
	 * @throws FileNotFoundException
	 */
	public static void createList(String fileName) throws FileNotFoundException {
		Scanner	input = new Scanner(new File(fileName));
		while (input.hasNext())
			bst.put(input.nextLine().toLowerCase(), 0);
		input.close();
	}
	
	/**
	 * Filters and array of Phrases based on a filterlist.
	 * Assumes that createList() has been called prior to this method.
	 * @param words The array of phrases to filter
	 * @return The array of filtered phrases
	 */
	public static Phrase[] filterList(Phrase[] words, int maxWords) {
		int currWord = 0;
		Phrase[] wordList = new Phrase[maxWords];
		
		for (Phrase word : words) {
			// exit loop if all the required words are found
			if (currWord >= maxWords) break;
			
			// if word does not exist in filterlist then add to filtered array
			if (!bst.contains(word.getString()) && currWord < maxWords)
				wordList[currWord++] = word;
		}
		
		return wordList;
	}
}