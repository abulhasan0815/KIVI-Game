import javax.swing.UIManager;

/**
 * The main method is the entry point of the application.
 * It creates a new instance of KiviGame to start the game.
 *
 * @author Group 4
 * 
 */

public class Kivi {
    public static void main(String[] args) {
        try
        {
           UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
        }
        catch (Exception e)
        {
           e.printStackTrace();

        }
        new KiviGame(); // Start the game by creating a new instance of KiviGame.
    }
}