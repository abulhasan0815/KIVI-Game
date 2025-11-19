import javax.swing.*;
import java.awt.*;

/**
 * Loads player info (name, stone count, score)
 *
 * @author Group 4
 * 
 */
public class PlayerInfoPanel {
    private JPanel playerPanel; // Panel to hold player information
    private int playerCount; // Number of players in the game
    private boolean hasComputerPlayer; // Whether the game includes a computer player
    private String computerDifficulty; // Difficulty level of the computer player
    public static String[] playerStones = {"O", "■", "▲", "X"}; // Stone symbols for players 1-4
    private JLabel[] stonesRemainingLabels;
    private JLabel[] playerLabels;

    // Constructor initializes the player information panel with the given parameters
    public PlayerInfoPanel(int playerCount, boolean hasComputerPlayer, String computerDifficulty) {
        this.playerCount = playerCount;
        this.hasComputerPlayer = hasComputerPlayer;
        
        playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));

    
        playerLabels = new JLabel[playerCount];
        stonesRemainingLabels = new JLabel[playerCount];

        this.computerDifficulty = computerDifficulty;
        // Create labels for each player
        for (int i = 0; i < playerCount; i++) {
            JPanel playerInfo = new JPanel();
            playerInfo.setLayout(new BoxLayout(playerInfo, BoxLayout.Y_AXIS));
        
            String playerName = (hasComputerPlayer && i == playerCount - 1) ? 
                            "Computer Player (" + computerDifficulty + ")" : 
                            "Player " + (i + 1);
        
            playerLabels[i] = new JLabel(playerName + " (" + playerStones[i] + ")");
            stonesRemainingLabels[i] = new JLabel("Turn remaining: 9/10{if consider the current one} [will be reduced to 0 at the end]");
        
            playerInfo.add(playerLabels[i]);
            playerInfo.add(stonesRemainingLabels[i]);
            playerPanel.add(playerInfo);
            playerPanel.add(Box.createVerticalStrut(10));
            
        }
        setupPlayerInfoPanel(); // Set up the player information panel
    }

    // Sets up the player information panel with player names and initial stats
    private void setupPlayerInfoPanel() {
        playerPanel = new JPanel(new GridLayout(playerCount, 1)); // Layout to stack player info vertically

        for (int i = 1; i <= playerCount; i++) {
            String playerName = "Player " + i + " (" + playerStones[i - 1] + ")"; // Default player name and symbol
            // If it's the last player and there is a computer player, append computer details
            if (hasComputerPlayer && i == playerCount) {
                playerName += " (Computer - " + computerDifficulty + ")";
            }

            playerPanel.add(createPlayerPanel(playerName)); // Add the player panel to the main panel
        }
    }

    // Creates a panel for a single player with their name, stones left, and points
    private JPanel createPlayerPanel(String playerName) {
        JPanel panel = new JPanel(new GridBagLayout()); // Panel for individual player info
        GridBagConstraints position = new GridBagConstraints(); // Constraints for layout manager
        position.gridx = 0; // Column position
        position.gridy = 0; // Row position
        panel.add(new JLabel(playerName), position); // Add player name label
        position.gridy = 1; // Move to next row
        panel.add(new JLabel("Stone Left : 10 [need to Configure]"), position); // Add points label
        return panel; // Return the created panel
    }
    
    // Method to update the stones remaining for a player
    public void updateTurnsRemaining(int playerIndex, int turnRemaining) {
        stonesRemainingLabels[playerIndex].setText("Turn left: " + turnRemaining);
    }

    // Getter method to retrieve the player information panel
    public JPanel getPlayerPanel() {
        return playerPanel;
    }
}