import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class statBE {
	
	//folder that contains all of the files
    private File folder;
    //array of all of the files
    private static File[] files;

    //constructor calls to generate the csv and assigns the folder path
    public statBE() {
    	
        folder = new File("../data/chat-sessions");
        files = folder.listFiles();
        generateCSV();

    }
    
    //generates the csv file based on all the files in the folder
    public void generateCSV() {
    	
    	//number of files
        int num = 1;
        
        String csvFileName = "../data/chat_statistics.csv";
        
        //if no files
        if (files == null) {
            return;
        }
        
        try (FileWriter writer = new FileWriter(csvFileName)) {
        	//header for the .csv file
            writer.write("S.No,chat_file,#user_utterance,#system_utterance,duration\n");
            //loops through each file
            for (File file: files) {
            	//ensures the file exists
                if (file.isFile()) {
                	//calls other methods to write to the .csv file all of the necessary stats
                    writer.write(num + "," + file.getName() + "," + findUser(file) + "," + findBot(file) + "," + findTimeTake(file) + "\n");
                }
                //add one to file number
                num++;
            }
            writer.close();
        } catch (IOException e) {
            progFE.print(true, "Could not open csv file");

        }
    }
    
    //finds the nummber of bot utterances in the file
    public static String findBot(File file) {
    	
        Scanner scan = null;
        
        //create scanner
        try {
            scan = new Scanner(file);
        } catch (FileNotFoundException e) {
            progFE.print(true, "Could not open file");
            return "0";
        }
        
        //loops through whole file
        while (scan.hasNext()) {
            String line = scan.nextLine();
            String[] splitLine = line.split(" ");
            //ensures that the split line is large enough
            if (splitLine.length > 3) {
            	//looks for the identifier that states how many times the bot spoke
                if (splitLine[0].equals("bot=") && splitLine[2].equals("userUtt=")) {
                    scan.close();
                    //returns the number after the indentifier
                    return splitLine[1];
                }
            }

        }
        scan.close();
        return "0";
    }
    
    //finds the number of user utterances in a file
    public static String findUser(File file) {
    	
        Scanner scan = null;
        
        //create scanner
        try {
            scan = new Scanner(file);
        } catch (FileNotFoundException e) {
            progFE.print(true, "Coud not open file");
            return "0";
        }
        
        //loops through whole file
        while (scan.hasNext()) {
            String line = scan.nextLine();
            String[] splitLine = line.split(" ");
            //ensures split line is large enough
            if (splitLine.length > 3) {
            	//looks for the identifier that states how many times the user spoke
                if (splitLine[0].equals("bot=") && splitLine[2].equals("userUtt=")) {
                    scan.close();
                    //returns the number after the indentifier
                    return splitLine[3];
                }
            }

        }
        scan.close();
        return "0";
    }

    //finds the time taken in the file
    public static String findTimeTake(File file) {

        Scanner scan = null;
        
        //creates a scanner
        try {
            scan = new Scanner(file);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "0";
        }
        
        //scans through the whole file
        while (scan.hasNext()) {
            String line = scan.nextLine();
            String[] splitLine = line.split(" ");
            //ensure the split line is even large enough to house the data
            if (splitLine.length > 2) {
            	//looks for the specific identifiers
                if (splitLine[0].equals("Time") && splitLine[1].equals("Taken:")) {
                    scan.close();
                    //returns the number after the identifier
                    return splitLine[2];
                }
            }

        }
        scan.close();
        return "0";
    }
    
    //loops through every file
    public static void doSummary() {
    	
    	//if there are no files
        if (files == null) {
            return;
        }
        
        //total numbers
        int numFiles = 0;
        int userUtt = 0;
        int botUtt = 0;
        int time = 0;
        
        //goes through every file and searches for the requested data and then adds it to the totals
        for (File file: files) {
            if (file.isFile()) {
                numFiles++;
                userUtt += Integer.parseInt(findUser(file));
                botUtt += Integer.parseInt(findBot(file));
                time += Integer.parseInt(findTimeTake(file));
            }
        }
        //print statement for all of it
        progFE.print(true, "There are " + numFiles + " chats with user asking " + userUtt + " times and the bot responding " + botUtt + " times. The total duration(s) was " + time);
    }
    
    //prints the summary for just one file
    public static void doShowChatSummary(int num) {
    	//ensures that the requested file number is within the actual number of files
        if (num > files.length || num < 1) {
            progFE.print(true, "Not that many files");
            return;
        }
        
        //prints and calls functions to search for requested data
        progFE.print(true, "Chat " + num + " has user asking " + findUser(files[num - 1]) + " times and system respond " + findBot(files[num - 1]) + " times. Duration(s): " + findTimeTake(files[num - 1]));
    }
    
    //prints the whole chat from one file
    public static void doShowChat(int num) {
    	//ensures that the requested file number is within the actual number of files
        if (num > files.length || num < 1) {
            progFE.print(true, "Not that many files");
            return;
        }
        
        Scanner scan = null;
        
        //creates scanner
        try {
            scan = new Scanner(files[num - 1]);
        } catch (FileNotFoundException e) {
            progFE.print(true, "Could not open the desired file");
        }
        
        //goes through each lines and prints them all
        while (scan.hasNext()) {
            progFE.print(true, scan.nextLine());
        }
        scan.close();
    }
}