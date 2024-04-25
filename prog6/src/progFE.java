import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

@SuppressWarnings("serial")
public class progFE extends JFrame implements ActionListener {
	
	//keep track of times the bot and user speak
	static int totalBotUtt=0;
	static int totalUserUtt=0;
	
	static long startTime = 0;
	
	//bot processor object
    private static progBE botProcessor;
    
	//GUI attributes
    static JTextArea output = new JTextArea();
    private static JTextField input = new JTextField();
    private static JScrollPane sp = new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    
    //GUI setup
     private progFE(String title) {
    	
    	// frame setup
        super(title);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(true);
        getContentPane().setBackground(Color.cyan);

        // Output area
        output.setEditable(false);
        output.setBackground(Color.lightGray);
        output.setFont(new Font("SansSerif", Font.PLAIN, 15));
        print(true, "What do you wish to search? Do -clear to clear the screen");
        
        // Scrollbar dimensions
        sp.setPreferredSize(new Dimension(775, 470)); 

        // Input area
        input.setText("Click to enter text here");
        input.setForeground(Color.black);
        input.setFont(new Font("Serif", Font.BOLD, 25));
        input.addActionListener(this);
        input.setBackground(Color.lightGray);
        
        //deletes input text when you click on input box
        input.addFocusListener((FocusListener) new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (input.getText().equals("Click to enter text here")) {
                    input.setText("");
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
            	input.setText("Click to enter text here");
            }
        });
        
        // Add components to the content pane with appropriate regions
        add(sp, BorderLayout.CENTER);
        add(input, BorderLayout.PAGE_END);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	
            	long timeTaken = System.nanoTime() - startTime;
            	timeTaken = (long) ((double) timeTaken / 1e9);
            	
            	progBE.doClosing(totalBotUtt, totalUserUtt, timeTaken);
                    dispose();
                    System.exit(0); 
            }
        });
    }
     
     
    //activates when input is detected
    public void actionPerformed(ActionEvent e) {
    	
    	//retrieves text from input box
        String userInput = input.getText();
       
        //prints initial user request
        print(false, userInput);
        //resets input box
        input.setText("");
        
        //Has the back end check the user input (is chit-chat or has a company name and valid key)
        if (botProcessor.checkInput(userInput)) {
        	//Calls the back end to create a data object to search for the related text and print it.
        	botProcessor.searchAndPrint();
        }
        
    }
    
    //print message with a parameter to print as the box or customer
    public static void print(boolean bot, String message) {
        String type;
        //decides printing type
        if (bot) {
            type = "bot";
            totalBotUtt++;
        } else {
            type = "me";
            totalUserUtt++;
        }
        //ensures message contains text
        if (!message.trim().equals("")) {
        	output.append(type + "-> " + message + "\n");
        }
    }
    
    
    //main method to start GUI FE and processor BE
    public static void main(String[] args) {
    	
    	//records the tiem the bot started
    	startTime = System.nanoTime();
    	
    	//starts GUI
        progFE bot = new progFE("Wendys and Restaurant Brands Int. Chat Bot");
        //starts the back end processor
        botProcessor = new progBE();
        
        //creates an icon image for GUI
        ImageIcon img = new ImageIcon("../data/Wendy's_full_logo_2012.svg.png");
        
        //creates small initial frame and icon image
    	bot.setIconImage(img.getImage());
        bot.setSize(800, 605);
        bot.setLocation(50, 50);


    }
    

}