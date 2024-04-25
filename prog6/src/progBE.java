import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class progBE {
	
	//matching threshold
    final static double THRESHOLD = 0.75;
    
    //current 10k file
    private File currentFile;

    //current filtered request
    private String request;

    //Current whole line request
    private String userInput;
    
    //current log file
    private static File logFile;
    
    //stat processor object
    private static statBE statProcessor;
    
    //constructor
    public progBE() {
    	 
         //starts the stat back end processor
         statProcessor = new statBE();
        
    	 //prints the date and time in output log files
    	 LocalDateTime currentDateTime = LocalDateTime.now();
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
         String formattedDateTime = currentDateTime.format(formatter);
         
         //log file location
         String fileName = "../data/chat-sessions/" + formattedDateTime + ".txt";
         
         //assigns the file path
         logFile = new File(fileName);
         
    }

    //Overall logic to check a new input. Will return false if the input is not valid and the program will stop it's process.
    public boolean checkInput(String newInput) {
    	
    	writeToLog(newInput);
    	
        //assigns class variable to new verification request
        this.userInput = newInput.toLowerCase();

        //ensures the new input is not just empty
        if (userInput.trim().equals("")) {
            return false;
        }

        //checks if user wants to reset screen
        if (checkClear()) {
            return false;
        }

        checkForChitChat();
        
        //ends if debug is detected
        if (checkForDebug()) {
        	return false;
        }
        
        //ends if scope is detected
        if(checkForScope()) {
        	return false;
        }
        
        //attempts to create the file based on given user input. 
        if (createFile()) {
            progFE.print(true, "File found!");
        } else {
            return false;
        }

        //attempts to create the request from the give user input by comparing different keys
        if (getCommand()) {
            progFE.print(true, "I understand, here is info on " + request + ": \n");
        } else {
            progFE.print(true, "I do not understand. Please try again");
            writeToLog("I do not understand. Please try again");
            return false;
        }

        return true;

    }
    
    //check for scope
    boolean checkForScope() {
    	
    	if(calculateOverlapRatio(userInput, "what companies") > THRESHOLD) {
    		progFE.print(true, "Wendys and Restaurant Brands International");
    		return true;
    	}
    	
    	if(calculateOverlapRatio(userInput, "what info") > THRESHOLD) {
    		for (int i = 0; i < validKeys.length; i++) {
    			progFE.print(true, validKeys[i][validKeys[i].length-1] + ", ");
    		}
    		return true;
    	}
    	return false;
    }
    
    //code taken from cody  Miller
    boolean checkForDebug() {
    	
    	Pattern patternChatSumm = Pattern.compile("showchat-summary [1-9]+", Pattern.CASE_INSENSITIVE); //These patterns will be used to search for the used commands later
		
		Pattern patternShowChat = Pattern.compile("showchat [1-9]+", Pattern.CASE_INSENSITIVE);
		
		Pattern patternChatNum = Pattern.compile("[0-9]+", Pattern.CASE_INSENSITIVE);
    	
		
		Matcher matcherChatSumm = patternChatSumm.matcher(userInput); //Create the matchers for the patterns
		
		Matcher matcherShowChat = patternShowChat.matcher(userInput); //Create the matchers for the patterns
		
		Matcher matcherChatNum = patternChatNum.matcher(userInput); //Create the matchers for the patterns
		
		if(matcherChatSumm.find() && matcherChatNum.find())
		{
			statBE.doShowChatSummary(Integer.parseInt(matcherChatNum.group())); //Print specific chat summary
			return true;
		}
		else if(matcherShowChat.find() && matcherChatNum.find())
		{
			statBE.doShowChat(Integer.parseInt(matcherChatNum.group())); //Print specific chat log
			return true;
		}
		else if(userInput.toLowerCase().contains("summary"))
		{
			statBE.doSummary();//print summary of chats
			return true;
		}
		
    	return false;
    }
    
    // Will search for desired section of the text via the creation of a data object and print it. This is called by the front end.
    public void searchAndPrint() {
        //creates the data object that will search for the filtered request from the desired 10k file
        data searchedData = new data(currentFile, request);
        //Separate print method.
        String data = searchedData.getOutput();
        printFoundOutput(data);
    }
    
   //writes chat to log file when the is closed
    public static void writeToLog(String message) {
    	FileWriter logWriter;
    	try {
    		logWriter = new FileWriter(logFile, true);
    		logWriter.write(message + "\n");;
        	logWriter.close();
    	} catch (IOException e1) {
            System.out.println("Could not open output file");
        }
    }
    
    //writes to log end statistics when the chat is closed
    public static void doClosing(int botUtt, int userUtt, long timeTaken) {
    	
    	writeToLog("bot= " + botUtt + " userUtt= " + userUtt + "\n");
    	writeToLog("Time Taken: " + timeTaken);
    
    }
    
    //prints data found from data object to the GUI and file
    private void printFoundOutput(String output) {

        //output file writer object
        FileWriter myWriter;
      
        //splits the data on lines based on sentences for readability and prints
        String splitOutput[] = output.split("\n");

        //attempts to print the output to the console and the output file. It splits the lines for filewriter to print properly.
        try {
            myWriter = new FileWriter("../data/output.txt");
            myWriter.write("I understand, here is info on " + request + ":");
            writeToLog("I understand, here is info on " + request + ":");
            for (String part: splitOutput) {
                myWriter.write(part + "\n");
                writeToLog(part);
                progFE.print(true, part);
            }
            progFE.print(true, "What do you wish to search?");
            writeToLog("What do you wish to search?");
            //closes writer object
            myWriter.close();
        } catch (IOException e1) {
            System.out.println("Could not open output file");
        }
    }

    //checks if the input requests to clear the screen
    private boolean checkClear() {
        if (userInput.equals("-clear")) {
            //resets to default message
            progFE.output.setText("bot->What do you wish to search? Do -clear to clear the screen\n");
            return true;
        }
        return false;
    }

    //method checks for non-query related input. returns true if it is found. 
    private void checkForChitChat() {

        //iterates through each possible key
        for (int i = 0; i < chitChat.length; i++) {

            Pattern pattern = Pattern.compile(chitChat[i][0], Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(userInput);

            if (matcher.find()) {
                //if a match is found, it is added to the list of found keys
                progFE.print(true, chitChat[i][1]);
                writeToLog(chitChat[i][1]);
            }
        }
    }

    //returns true if the file can be found based on the given input
    private boolean createFile() {

        //checks for possible, but obvious spellings and misspellings of the company names
        String regex = "w...y";
        String regex2 = "(?:restaurant|brand|international)";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Pattern pattern2 = Pattern.compile(regex2, Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(userInput);
        Matcher matcher2 = pattern2.matcher(userInput);

        try {
            //Checks to see which file name the input is close to and creates the file based on the 10k file location
            if (matcher.find()) {
                currentFile = new File("../data/Wendys.txt");
                return true;
            } else if (matcher2.find()) {
                currentFile = new File("../data/restBrands.txt");
                return true;
            } else {
                //in case no name is recognized
                progFE.print(true, "Please enter a company name atleast");
                writeToLog("Please enter a company name atleast");
                return false;
            }

        } catch (Exception e) {
            progFE.print(true, "There was a problem loading the file ");
            return false;
        }
    }

    //returns true if the command can be found based on a valid set of keys. Has the scanner object for reading input.
    private boolean getCommand() {
    	
    	//best match found dynamically
        String bestMatch = "";
        
        //best match data
        double currentSimilarity = 0;
        String currentMatchKey = "";
        
        //goes through every key and calculates overlap
        for (int j = 0; j < validKeys.length; j++) {
            for (int i = 0; i < validKeys[j].length - 1; i++) {
            	
            	//calculates similarity ratio for each key
                double newSimilarity = calculateOverlapRatio(userInput, validKeys[j][i]);

                //Continues if the new similarity is greater than the current and above the threshold or if the distances are equivalent and the new key is longer than the current.
                if ((newSimilarity > currentSimilarity && newSimilarity > THRESHOLD) || (currentSimilarity != 0 && newSimilarity == currentSimilarity && validKeys[j][i].length() > currentMatchKey.length())) {
                    bestMatch = validKeys[j][validKeys[j].length - 1];
                    currentSimilarity = newSimilarity;
                } 
            }
        }
        
        //if the match was not found
        if (bestMatch.equals("")) {
        return false; // No match found for any word
        }
        
        //set the request to the best found match
        request = bestMatch;
        return true;
    }



    //Calculates the ratio of the longest found subsequence of characters divided by the length of the key.
    private double calculateOverlapRatio(String str1, String str2) {
    	
    	//longest common continuous subsequence
        int commonSubsequenceLength = subStringTester(str1, str2);
        
        //takes the smaller of the two
        int shorterLength = Math.min(str1.length(), str2.length());
        
        //returns the ratio
        return (double) commonSubsequenceLength / shorterLength;
    }

    //gets the longest continuous substring
    private int subStringTester(String string1, String string2) {
    	
    	//2d array
        int[][] lc = new int[string1.length() + 1][string2.length() + 1];
        
        //longest common substring
        int largest = 0; 

        // Calculate the length of the common substring
        for (int i = 1; i <= string1.length(); i++) {
            for (int j = 1; j <= string2.length() ; j++) {
                if (string1.charAt(i - 1) == string2.charAt(j - 1)) {
                	lc[i][j] = lc[i - 1][j - 1] + 1;
                    largest = Integer.max(largest, lc[i][j]); 
                } else {
                	lc[i][j] = 0; 
                }
            }
        }

        return largest;
    }
    
    //Possible commands and their misspellings, associates the possible commands to items and parts in the text

    private static final String[][] validKeys = {
    		{"part 1", "part i", "Part I"},
            {"risk", "dangers", "look out for", "factoring risk", "item 1a", "Item 1A. Risk Factors"},
            {"staff", "workers", "employees", "item 1b", "Item 1B. Unresolved Staff Comments"},
            {"business", "item 1", "Item 1. Business"},
            {"property", "land", "locations", "how many restaurants", "Item 2. Properties"},
            {"legal", "law", "item 3", "Item 3. Legal Proceedings"},
            {"safety", "mine", "item 4", "disclosure", "Item 4. Mine Safety Disclosures"},
            {"part 2", "part ii", "Part II"},
            {"registrant", "item 5", "Item 5. Market for Registrant’s Common Equity, Related Stockholder Matters and Issuer Purchases of Equity Securities"},
            {"reserved", "item 6", "Item 6. Reserved"},
            {"discussion","analysis", "financial condition", "item 7", "operations", "Item 7. Management’s Discussion and Analysis of Financial Condition and Results of Operations"},
            {"market risk", "quantitative", "qualitative", "item 7a", "Item 7A. Quantitative and Qualitative Disclosures About Market Risk"},
            {"financial", "statements", "supplementary data", "item 8", "Item 8. Financial Statements and Supplementary Data"},
            {"changes", "disagreements with accountants", "item 9", "Item 9. Changes in and Disagreements with Accountants on Accounting and Financial Disclosure"},
            {"controls", "procedures", "item 9a", "Item 9A. Controls and Procedures"},
            {"other", "item 9b", "Item 9B. Other Information"},
            {"foreign", "inspections", "item 9c", "Item 9C. Disclosure Regarding Foreign Jurisdictions that Prevent Inspections"},
            {"part 3", "part iii", "Part III"},
            {"directors", "ceo", "executives", "corporate", "item 10", "Item 10. Directors, Executive Officers and Corporate Governance"},
            {"compensation", "item 11", "Item 11. Executive Compensation"},
            {"ownership", "stockholder", "item 12", "Item 12. Security Ownership of Certain Beneficial Owners and Management and Related Stockholder Matters"},
            {"relationships", "transactions", "independence", "item 13", "Item 13. Certain Relationships and Related Transactions, and Director Independence"},
            {"accounting fees", "services", "item 14", "Item 14. Principal Accounting Fees and Services"},
            {"part 4", "part iv", "Part IV"},
            {"exhibits", "schedules", "item 15", "Item 15. Exhibits and Financial Statement Schedules"},
            {"summary", "generalize this", "item 16", "Item 16. Form 10-K Summary"},
            {"all info", "everything", "all information"}
  
        };

    
     //some possible chit-chat to look for in regex.
     private static final String[][] chitChat = {
    	    		{"(?:hello|hi|hey)", "Hello there" },
    	    		{"(?:how are you|are you good|are you good|hows it going)", "Good, thanks!" },
    	    		{"(?:thanks|thank you|you're welcome)", "you're welcome"}
    	    		
  
        };
} 