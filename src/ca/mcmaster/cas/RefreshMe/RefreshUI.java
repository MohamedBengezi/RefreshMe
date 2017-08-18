package ca.mcmaster.cas.RefreshMe;

import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

/**
 * This class creates a swing application and initializes it with all of
 * its components and their actions. All of the UI is written here, and it
 * calls the outside functions which read from the dataset, and process the
 * data.
 * 
 * @author vanravec
 */

@SuppressWarnings("serial")
public class RefreshUI extends JFrame {
	// The messageLog and phraseList string buffers
	private String messageString = "";
	private String phraseString = "";
	
	// These are the components which need a global access level in this class
	private JButton refreshMeButton;
	private JTextArea phraseList;
	private JTextArea messageLog;
	private JTextField dayInput;
	private JTextField numWordsInput;
	
	/**
	 * This constructor initializes the UI elements as well as the JFrame
	 */
	public RefreshUI() {
        initUI();
        setTitle("RefreshMe");
        setSize(700, 515);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

	/**
	 * This method sets up and configures all of the UI elements and their
	 * respective actions.
	 */
    private void initUI() {
    	// Main JPanel
    	JPanel pane = new JPanel();
    	pane.setLayout(null);
    	add(pane);
    	
    	// RefreshMe button
    	refreshMeButton = new JButton("RefreshMe!");
        refreshMeButton.addActionListener((ActionEvent event) -> {
        	phraseString = "";
        	phraseList.setText(phraseString);
        	refreshMeButton.setEnabled(false);
        	
			Phrase[] popularWords = null;
        	
            // run the runner
			try {
				updateLog("Starting process.\nContacting the hipsters...\n");
				String dayIn = dayInput.getText();
				String wordsIn = numWordsInput.getText();
				
				// Check for errors
				if (!dayIn.matches("[-+]?\\d*\\.?\\d+")) {	// day given is not a number
					throw new Exception("Day input must be a number.");
				} else if (Integer.parseInt(dayIn) > 30 || Integer.parseInt(dayIn) < 1) { // day is out of range
					throw new Exception("Day input must be a between 1 and 30.");
				} else if (!wordsIn.matches("[-+]?\\d*\\.?\\d+")) { // numWords given is not a number
					throw new Exception("numWords input must be a number.");
				} else if (Integer.parseInt(wordsIn) < 1) {	// numWords given is negative
					throw new Exception("numWords must be a greater than 1.");	
				} else {
					// Get popularWords
					popularWords = RefreshMeRunner.getRefreshList(Integer.parseInt(dayIn), Integer.parseInt(wordsIn));
					
					// Exit process normally
					updateLog("Process completed normally.\n-----------------------------------------\n");
				}
			} catch (Exception e) {
				// Process ended with error - display the error
				refreshMeButton.setEnabled(true);
				updateLog("Process exited with error:\n" + e.toString() + "\n-----------------------------------------\n");
			}
			// Display the results
			int rank = 1;
			for (Phrase word : popularWords)
				phraseString += rank++ + ". " + word.getString() + "\t" + word.getFreq() + " uses found." + "\nExample: \"" + word.getExample() + "\"\n\n";
			phraseList.setText(phraseString);
        	
        	refreshMeButton.setEnabled(true);
        });
        refreshMeButton.setBounds(20,20,120,40);
        
        // dayInput Label
        JLabel dayLabel = new JLabel("Day: ");
        dayLabel.setBounds(320,20,30,20);
        
        // Day Input
        dayInput = new JTextField("1");
        dayInput.setEditable(true);
        dayInput.setToolTipText("The day of the month to query (1-30)");
        dayInput.setBounds(350,20,60,25);
        
        // numWords Label
        JLabel wordsLabel = new JLabel("numWords: ");
        wordsLabel.setBounds(278,50,80,20);
        
        // numWords Input
        numWordsInput = new JTextField("3");
        numWordsInput.setEditable(true);
        numWordsInput.setToolTipText("The number of unique words to display");
        numWordsInput.setBounds(350,50,60,25);
        
        // Word Output Label
        JLabel phraseLabel = new JLabel("Fresh Topics");
        phraseLabel.setBounds(20,80,200,20);
        
        // Word Output
        phraseList = new JTextArea();
        phraseList.setMargin(new Insets(5,5,5,5));
        phraseList.setEditable(false); 
        phraseList.setLineWrap(true);
        phraseList.setWrapStyleWord(true);
        JScrollPane phraseScroll = new JScrollPane(phraseList); // Scrollpane for text area
        phraseScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        phraseScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        phraseScroll.setBounds(20,100,440,355);
        
        // Message Logger Label
        JLabel loggerLabel = new JLabel("Message Log");
        loggerLabel.setBounds(480,80,200,20);
        
        // Message Logger
        messageLog = new JTextArea(messageString);
        messageLog.setMargin(new Insets(5,5,5,5));
        messageLog.setEditable(false);
        messageLog.setLineWrap(true);
        messageLog.setWrapStyleWord(true);
        JScrollPane logScroll = new JScrollPane(messageLog); // Scrollpane for textarea
        logScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        logScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        logScroll.setBounds(480,100,200,320);
        
        // Clear Button
        JButton clearLogButton = new JButton("Clear Logger");
        clearLogButton.setToolTipText("Clears the message log area");
        clearLogButton.addActionListener((ActionEvent event) -> {
        	// Clear the logger
        	clearLog();
        });
        clearLogButton.setBounds(480,424,200,30);
        
        // Add to filterlist label
        JLabel addToFilterLabel = new JLabel("Add to filterlist");
        addToFilterLabel.setBounds(480,30,100,20);
        
        // Add to filterlist textfield
        JTextField addToFilterField = new JTextField();
        addToFilterField.setBounds(480,50,130,25);
        
        // Add to filterlist button
        JButton addToFilterButton = new JButton("Add");
        addToFilterButton.setToolTipText("Adds the given word to the filter list");
        addToFilterButton.addActionListener((ActionEvent event) -> {
        	if (!addToFilterField.getText().equals(""))
        		addWordToFilter(addToFilterField.getText());
        	else
        		messageString += "Cannot add an empty string to the filter list!\n-----------------------------------------\n";
        	messageLog.setText(messageString);
        	addToFilterField.setText("");
        });
        addToFilterButton.setBounds(620,50,60,24);
        
        // Add components to JPanel
        pane.add(refreshMeButton);
        pane.add(dayLabel);
        pane.add(dayInput);
        pane.add(wordsLabel);
        pane.add(numWordsInput);
        pane.add(phraseLabel);
        pane.add(phraseScroll);
        pane.add(loggerLabel);
        pane.add(logScroll);
        pane.add(clearLogButton);
        pane.add(addToFilterLabel);
        pane.add(addToFilterField);
        pane.add(addToFilterButton);
    }
    
    /**
     * Appends the given text to the message logger
     * @param text the string to append to the logger
     */
    private void updateLog(String text) {
    	messageString += text;
    	messageLog.setText(messageString);
    }
    
    /**
     * Clears the message logger and its buffer
     */
    private void clearLog() {
    	messageString = "";
    	messageLog.setText(messageString);
    }
    
    /**
     * Adds a word to the filter list
     * @param word the word to add
     */
    private void addWordToFilter(String word) {
    	BufferedWriter bw = null;
    	try {
			bw = new BufferedWriter(new FileWriter("data/filter.txt", true));
			bw.write(word);
			messageString += "Added \"" + word + "\" to the filter list.\n-----------------------------------------\n";
		} catch (IOException e) {
			messageString += "Could not find filter.txt\n-----------------------------------------\n";
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }

    /**
     * The main function which invokes the swing application
     */
    public static void main(String[] args) {
	    EventQueue.invokeLater(() -> {
	        RefreshUI ui = new RefreshUI();
	        ui.setVisible(true);
	    });
    }
}