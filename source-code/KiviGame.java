import javax.swing.*;
import javax.swing.UIManager;


/**
 * Sets up the frame and calls for MainMenu
 *
 * @author Group 4
 * 
 */

public class KiviGame {
    private JFrame frame; // Main game window
    private MainMenu mainMenu; // Main menu panel

    public KiviGame() {
        setupMainMenu(); // Initialize and display the main menu
    }

    private void setupMainMenu() {
        try
        {
           UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
        }
        catch (Exception e)
        {
           e.printStackTrace();

        }
        frame = new JFrame("Kivi Game"); // Create the main game window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close application on exit
        frame.setSize(900, 815); // Set window size
        frame.setLocation(310, 0); // Set window position

        mainMenu = new MainMenu(frame); // Initialize main menu with reference to frame
        frame.add(mainMenu.getPanel()); // Add main menu panel to the frame
        frame.setVisible(true); // Make the window visible
    }

    public static void main(String[] args) {
        new KiviGame(); // Start the game
    }
}
