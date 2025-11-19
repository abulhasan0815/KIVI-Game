import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * Sets up Main Menu for game mode and settings
 *
 * @author Group 4
 * 
 */
public class MainMenu {
    private JFrame frame; // Main frame of the application
    private JPanel panel; // Panel for the main menu
    private int playerCount; // Number of players in the game
    private boolean hasComputerPlayer; // Whether the game includes a computer player
    private String computerDifficulty; // Difficulty level of the computer player
    private boolean monochrome; // Whether the game is in monochrome mode
    private boolean protanopia; // Whether the game is in protanopia mode
    private boolean tritanopia; // Whether the game is in tritanopia mode

    // Constructor initializes the main menu
    public MainMenu(JFrame frame) {
        try
        {
           UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
        }
        catch (Exception e)
        {
           e.printStackTrace();

        }
        this.frame = frame;
        setupMainMenu(); // Set up the main menu components
    }

    // Sets up the main menu with buttons and layout
    private void setupMainMenu() {
        JButton newGameButton = new JButton("Start New Game"); // Button to start a new game
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGameSetupDialog(); // Show game setup dialog when button is clicked
            }
        });
        JButton resumeGameButton = new JButton("Resume Game"); // Button to resume a game
        resumeGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resumeGame(); // Call the resume game method
            }
        });
        resumeGameButton.setEnabled(checkForSavedGame());

        panel = new JPanel(new GridBagLayout()); // Panel for main menu layout
        GridBagConstraints position = new GridBagConstraints(); // Constraints for layout manager
        position.gridx = 0; // Column position
        position.gridy = 0; // Row position
        panel.add(newGameButton, position); // Add new game button to panel
        position.gridy = 1; // Move to next row
        panel.add(resumeGameButton, position); // Add resume game button to panel
    }

    // Getter method to retrieve the main menu panel
    public JPanel getPanel() {
        return panel;
    }

    private boolean checkForSavedGame() {
        File saveFile = new File("SavedKiviGame.ser");
        return saveFile.exists();
    }

    private void resumeGame() {
        try (FileInputStream fileIn = new FileInputStream("SavedKiviGame.ser");
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            GameState savedState = (GameState) in.readObject();
            frame.getContentPane().removeAll();
            new GameBoard(frame, savedState.getPlayerCount(), savedState.hasComputerPlayer(),
                    savedState.getComputerDifficulty(), savedState.isMonochrome(), savedState.isProtanopia(),
                    savedState.isTritanopia(), savedState);
            frame.revalidate();
            frame.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading saved game!", "Resume Game", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Shows the game setup dialog for configuring game settings
    private void showGameSetupDialog() {
        JDialog setupDialog = new JDialog(frame, "Kivi Game Setup", true); // Modal dialog for game setup
        setupDialog.setLayout(new GridBagLayout()); // Layout manager for dialog
        GridBagConstraints gbc = new GridBagConstraints(); // Constraints for layout manager
        gbc.insets = new Insets(10, 10, 10, 10); // Padding for components

        // Add Number of Players selection
        JComboBox<String> playerComboBox = showPlayerSelectionDialog(setupDialog, gbc);

        // Add Computer Player selection
        JRadioButton withComputerRadio = showComputerPlayerDialog(setupDialog, gbc);

        // Add Computer Difficulty selection
        JComboBox<String> difficultyComboBox = showComputerDifficultyDialog(setupDialog, gbc, withComputerRadio);

        // Add Game Colors selection
        JComboBox<String> monoComboBox = showMonochormaticDialog(setupDialog, gbc);

        // Add Start Game button
        JButton startButton = new JButton("Start Game"); // Button to start the game
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Set player count
                playerCount = playerComboBox.getSelectedIndex() + 2;

                // Set computer player option based on radio button
                hasComputerPlayer = withComputerRadio.isSelected();

                // Set computer difficulty (only if computer player is selected)
                computerDifficulty = hasComputerPlayer ? 
                        (difficultyComboBox.getSelectedIndex() == 0 ? "Easy" : "Hard") : null;

                // Set monochromatic mode
                protanopia = (monoComboBox.getSelectedIndex() == 1);
                tritanopia = (monoComboBox.getSelectedIndex() == 2);
                monochrome = (monoComboBox.getSelectedIndex() == 3);

                // Start the game
                new GameBoard(frame, playerCount, hasComputerPlayer, computerDifficulty, monochrome, protanopia, tritanopia);
                setupDialog.dispose(); // Close the setup dialog
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        setupDialog.add(startButton, gbc); // Add start game button to dialog

        // Set dialog properties
        setupDialog.pack(); // Adjust size to fit components
        setupDialog.setLocationRelativeTo(frame); // Center dialog relative to frame
        setupDialog.setVisible(true); // Show dialog
    }

    // Method to add Number of Players selection to the setup dialog
    private JComboBox<String> showPlayerSelectionDialog(JDialog setupDialog, GridBagConstraints gbc) {
        JLabel playerLabel = new JLabel("Number of Players:"); // Label for player selection
        String[] playerOptions = {"2 Players", "3 Players", "4 Players"}; // Options for number of players
        JComboBox<String> playerComboBox = new JComboBox<>(playerOptions); // Combo box for player selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        setupDialog.add(playerLabel, gbc); // Add label to dialog
        gbc.gridx = 1;
        setupDialog.add(playerComboBox, gbc); // Add combo box to dialog
        return playerComboBox; // Return the combo box
    }

    // Method to add Computer Player selection to the setup dialog
    private JRadioButton showComputerPlayerDialog(JDialog setupDialog, GridBagConstraints gbc) {
        JLabel computerLabel = new JLabel("Computer Player:"); // Label for computer player selection
        JPanel computerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Panel for radio buttons
        JRadioButton withComputerRadio = new JRadioButton("Yes"); // Radio button for "Yes"
        JRadioButton noComputerRadio = new JRadioButton("No", true); // Radio button for "No" (default selected)
        ButtonGroup computerGroup = new ButtonGroup(); // Button group for radio buttons
        computerGroup.add(noComputerRadio);
        computerGroup.add(withComputerRadio);
        computerPanel.add(noComputerRadio);
        computerPanel.add(withComputerRadio);
        gbc.gridx = 0;
        gbc.gridy = 1;
        setupDialog.add(computerLabel, gbc); // Add label to dialog
        gbc.gridx = 1;
        setupDialog.add(computerPanel, gbc); // Add radio buttons to dialog
        return withComputerRadio; // Return the "Yes" radio button
    }

    // Method to add Computer Difficulty selection to the setup dialog
    private JComboBox<String> showComputerDifficultyDialog(JDialog setupDialog, GridBagConstraints gbc, JRadioButton withComputerRadio) {
        JLabel difficultyLabel = new JLabel("Computer Difficulty:"); // Label for difficulty selection
        String[] difficultyOptions = {"Easy Mode", "Hard Mode"}; // Options for difficulty
        JComboBox<String> difficultyComboBox = new JComboBox<>(difficultyOptions); // Combo box for difficulty selection
        difficultyComboBox.setEnabled(false); // Disable combo box initially
        gbc.gridx = 0;
        gbc.gridy = 2;
        setupDialog.add(difficultyLabel, gbc); // Add label to dialog
        gbc.gridx = 1;
        setupDialog.add(difficultyComboBox, gbc); // Add combo box to dialog

        // Enable/disable difficulty combo box based on computer player radio selection
        withComputerRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                difficultyComboBox.setEnabled(withComputerRadio.isSelected());
            }
        });
        return difficultyComboBox; // Return the combo box
    }

    // Method to add Game Colors selection to the setup dialog
    private JComboBox<String> showMonochormaticDialog(JDialog setupDialog, GridBagConstraints gbc) {
        JLabel monoLabel = new JLabel("Game Colors:"); // Label for game colors selection
        String[] monoOptions = {"Normal Mode", "No RED Mode", "No Blue Mode", "Black and White Mode"}; // Options for game colors
        JComboBox<String> monoComboBox = new JComboBox<>(monoOptions); // Combo box for game colors selection
        gbc.gridx = 0;
        gbc.gridy = 3;
        setupDialog.add(monoLabel, gbc); // Add label to dialog
        gbc.gridx = 1;
        setupDialog.add(monoComboBox, gbc); // Add combo box to dialog
        return monoComboBox; // Return the combo box
    }
}