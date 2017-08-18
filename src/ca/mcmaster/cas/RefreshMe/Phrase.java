package ca.mcmaster.cas.RefreshMe;

/**
 * This class provides an abstract data type for conviently storing the popular
 * word, its frequency as well as an example quote using this word. It implements
 * the Comparable interface so that it can properly be compared and sorted by its
 * frequency.
 * 
 * @author bengezim
 */

public class Phrase implements Comparable<Phrase> {
	// Private fields
	private String phrase;
	private Integer freq;
	private String example;
	
	/**
	 * Constructor for the Phrase object
	 * @param s the word to store
	 * @param freq the frequency of the word
	 */
	public Phrase(String s, int freq) {
		this.phrase = s;
		this.freq = freq;
	}
	
	/**
	 * Getter for the popular word
	 * @return the popular word 
	 */
	public String getString() {
		return phrase;
	}
	
	/**
	 * Sets the popular word
	 * @param input the new word
	 */
	public void setString(String input) {
		phrase = input;
	}
	
	/**
	 * Getter for the frequency of the word
	 * @return the frequency
	 */
	public Integer getFreq() {
		return freq;
	}
	
	/**
	 * Sets the frequency of the word
	 * @param freq the new frequency
	 */
	public void setFreq(int freq) {
		this.freq = freq;
	}
	
	/**
	 * Sets the example quote
	 * @param ex the new example quote
	 */
	public void setExample(String ex) {
		example = ex;
	}
	
	/**
	 * Getter for the example quote
	 * @return the example quote
	 */
	public String getExample() {
		return example;
	}
	
	/**
	 * Compares to another Phrase object based upon their frequencies.
	 * @param o the other Phrase ojbect
	 * @return 1 if greater, -1 if less, and 0 if equal
	 */
	@Override
	public int compareTo(Phrase o) {
		return freq.compareTo(o.getFreq());
	}
}