import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class data {

    //current 10k file
    private File currentFile;

    //current request
    private String request;

    //output string that will be retrieved after related text is parsed.
    private String output = "";

    //constructor(file and request have to be valid so no need for a check)
    data(File currentFile, String request) {

        this.currentFile = currentFile;
        this.request = (request.toLowerCase());

        //initializes search
        scanForOutput();

    }

    //getter for text
    public String getOutput() {
        return output;
    }

    //looks for desired item
    private void scanForItem(Scanner scan, String line, String[] splitLine) {
        do {
            //adds the line to the output variable
            output += " " + line + "\n";
            //makes sure the scanner has not hit the end of the file
            if (scan.hasNext()) {
                line = scan.nextLine();
            } else {
                break;
            }

            //ensures line is not empty, so it does not try to access an empty array.
            if (line.trim().equals("")) {
                continue;
            }
            splitLine = line.toLowerCase().split(" ");
            //goes until it hits another item or part
        } while (!splitLine[0].contains("item") && !splitLine[0].contains("part"));
    }

    //looks for desired part
    private void scanForPart(Scanner scan, String line, String[] splitLine) {
        do {
            //adds the line to the output variable
            output += " " + line + "\n";
            //makes sure the scanner has not hit the end of the file
            if (scan.hasNext()) {
                line = scan.nextLine();
            } else {
                break;
            }
            //ensures line is not empty, so it does not try to access an empty array.
            if (line.trim().equals("")) {
                continue;
            }
            splitLine = line.toLowerCase().split(" ");
            //goes until it hits another part
        } while (!splitLine[0].contains("part"));
    }

    //creates scanner object and starts a scan for the desired part or item
    private void scanForOutput() {

        //scanner object
        Scanner scan;

        //ensures scanner object can be opened.
        try {
            scan = new Scanner(currentFile);
        } catch (FileNotFoundException e) {
            System.out.println("File could not be found when searching");
            return;
        }
        if (request.equals("all information")) {
            while (scan.hasNext()) {
                output += scan.nextLine() + "\n";
            }
            scan.close();
            return;
        }
        //goes through each line to see if the line matches the requested section
        while (scan.hasNext()) {

            String line = scan.nextLine();
            String[] splitLine = {" "};

            //Wendys adds periods at the end of items so it checks for this
            if (line.trim().toLowerCase().equals(request) || line.trim().toLowerCase().equals(request + ".")) {

                //starts respective search

                if (request.contains("item"))
                    scanForItem(scan, line, splitLine);

                if (request.contains("part"))
                    scanForPart(scan, line, splitLine);
            }
        }
        //closes scanner object
        scan.close();
    }
}