import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;



/**
 * Sets up the Game Board and rolls dice
 *
 * @author Group 4
 * 
 */
public class GameBoard {
    private ComputerPlayer computerPlayer; // Handles computer player logic
    private static final int GRID_SIZE = 7; // Size of the game grid
    private JButton[][] gridButtons; // Buttons representing the grid cells
    private String[][] boardCombinations; // Combinations for each grid cell
    private int[][] boardPoints; // Points for each grid cell
    private int[][] stonePlacement; // Placement of stones on the grid
    private Random random = new Random(); // Random number generator
    private int[] diceRolls = new int[6]; // Array to store dice rolls

    private JFrame frame; // Main frame of the game
    private JPanel boardPanel; // Panel for the game board
    private JPanel bottomPanel; // Panel for bottom controls
    private int playerCount; // Number of players
    private boolean hasComputerPlayer; // Whether there is a computer player
    private String computerDifficulty; // Difficulty level of the computer player
    private boolean monochrome; // Whether the game is in monochrome mode
    private boolean protanopia; // Whether the game is in protanopia mode
    private boolean tritanopia; // Whether the game is in tritanopia mode
    private boolean hasRolled; // Whether the dice have been rolled
    private Validation validation; // Instance of Validation for dice checks
    private int currentPlayer = 1; // Track current player (1 to playerCount)
    private int displacedRow = -1; // Track displaced stone position
    private JLabel turnInfoLabel; // Label to display current player and turns left
    private int totalTurns = 10; // Example: total turns per game, adjust as needed
    private int turnsLeft = totalTurns; // Tracks remaining turns
    private int displacedPlayer = -1; // Tracks the player whose stone was displaced
    private int[] turnsRemaining; // Array to track stones remaining for each player
    private PlayerInfoPanel playerInfoPanel; // Panel to display player information
    
     
    
    // Constructor for loading Saved Game
    public GameBoard(JFrame frame, int playerCount, boolean hasComputerPlayer, String computerDifficulty, 
    boolean monochrome, boolean protanopia, boolean tritanopia, GameState savedState) {
        try
        {
           UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
        }
        catch (Exception e)
        {
           e.printStackTrace();

        }
        this.frame = frame;
        this.playerCount = playerCount;
        this.hasComputerPlayer = hasComputerPlayer;
        this.computerDifficulty = computerDifficulty;
        this.monochrome = monochrome;
        this.protanopia = protanopia;
        this.tritanopia = tritanopia;

        this.turnsRemaining = new int[playerCount];
        this.gridButtons = new JButton[GRID_SIZE][GRID_SIZE];
        this.boardCombinations = new String[GRID_SIZE][GRID_SIZE];
        this.boardPoints = new int[GRID_SIZE][GRID_SIZE];
        this.stonePlacement = new int[GRID_SIZE][GRID_SIZE];

    
        if (hasComputerPlayer) {
            computerPlayer = new ComputerPlayer(computerDifficulty);
        }
        
        if (savedState != null) {
            loadGameState(savedState);
        } else {
            // Initialize new game
            for (int i = 0; i < playerCount; i++) {
                turnsRemaining[i] = 10;
            }
            CellManager.initializeBoard(boardCombinations, boardPoints, random);
            validation = new Validation(diceRolls);
        }

        setupGameBoard();
        if (savedState != null) {
            updateUIFromState(); // Update UI only if loading
        }
    }

    // Default constructor (for new game)
    public GameBoard(JFrame frame, int playerCount, boolean hasComputerPlayer, String computerDifficulty, 
    boolean monochrome, boolean protanopia, boolean tritanopia) {
        this(frame, playerCount, hasComputerPlayer, computerDifficulty, monochrome, protanopia, tritanopia, null);
    }

     // Method to set up the game board
     private void setupGameBoard() {
        frame.getContentPane().removeAll(); // Clear the frame content
        frame.setLayout(new BorderLayout()); // Set layout to BorderLayout

        JPanel topPanel = new JPanel(); // Panel for top controls
        JButton resetButton = new JButton("Reset"); // Reset button
        resetButton.addActionListener(new ActionListener() { // Add action listener for reset
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame(); // Call reset method
            }
        });
        JButton saveButton = new JButton("Save"); // Save button
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGame(); // Call the save method
            }
        });
        turnInfoLabel = new JLabel("Player " + currentPlayer + "'s Turn (" + PlayerInfoPanel.playerStones[currentPlayer - 1] + ") - Turns Left: " + turnsLeft); // Current Player's Info
        topPanel.add(resetButton); // Add reset button to top panel
        topPanel.add(saveButton); // Add save button to top panel
        topPanel.add(turnInfoLabel); // Add turn info label to top panel
        frame.add(topPanel, BorderLayout.NORTH); // Add top panel to frame



        boardPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE)); // Panel for the game board
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                gridButtons[row][col] = new JButton(); // Create button for each cell
                gridButtons[row][col].setText(boardCombinations[row][col]); // Set text to combination
                gridButtons[row][col].setFont(new Font("Arial", Font.BOLD, 15));
                Color cellColor = CellManager.getCellColor(row, col, monochrome, protanopia, tritanopia);
                gridButtons[row][col].setBackground(cellColor); // Set initial background colors    
                gridButtons[row][col].setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Set border for each button
                final int finalRow = row; // Final row for lambda expression
                final int finalCol = col; // Final column for lambda expression
                gridButtons[row][col].addActionListener(new ActionListener() { // Add action listener to each button
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handleCellClick(finalRow, finalCol); // Handle cell click
                    }
                });
                boardPanel.add(gridButtons[row][col]); // Add button to board panel
            }
        }
        boardPanel.setBackground(Color.WHITE); // Set background color of board panel
        frame.add(boardPanel, BorderLayout.CENTER); // Add board panel to frame

        // Bottom panel with Dice Roll button
        bottomPanel = new JPanel();
        JButton rollButton = new JButton("Roll Dice"); // Roll dice button
        rollButton.setFont(new Font("Arial", Font.BOLD, 20)); // Increase font size
        rollButton.setPreferredSize(new Dimension(150, 50)); // Set a larger preferred size
        rollButton.addActionListener(new ActionListener() { // Add action listener to roll button
            @Override
            public void actionPerformed(ActionEvent e) {
                rollDice(); // Roll dice
            }
        });
        bottomPanel.add(rollButton); // Add roll button to bottom panel

        // Panel to show dice rolls and instructions
        JPanel rollResultPanel = new JPanel();
        rollResultPanel.setLayout(new BoxLayout(rollResultPanel, BoxLayout.Y_AXIS)); // Set layout to BoxLayout
        bottomPanel.add(rollResultPanel); // Add roll result panel to bottom panel
        frame.add(bottomPanel, BorderLayout.SOUTH); // Add bottom panel to frame

        // Setup player information panel
        playerInfoPanel = new PlayerInfoPanel(playerCount, hasComputerPlayer, computerDifficulty);
        frame.add(playerInfoPanel.getPlayerPanel(), BorderLayout.WEST); // Add player info panel to frame

        frame.add(new JPanel(), BorderLayout.EAST); // Add empty panel to east
        frame.revalidate(); // Re validate the frame
        frame.repaint(); // Repaint the frame
    }


    // Method to update UI after loading a saved state

    private void updateUIFromState() {
        // Update grid buttons to reflect stonePlacement
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int player = stonePlacement[row][col];
                if (player != 0) {

                    int tempPlayer = currentPlayer;
                    currentPlayer = player; // Temporarily set to the player who owns the stone
                    placeStone(row, col);   // Call placeStone to update button
                    currentPlayer = tempPlayer; // Restore current player
                } else {
                    gridButtons[row][col].setText(boardCombinations[row][col]); 
                    gridButtons[row][col].setBackground(CellManager.getCellColor(row, col, monochrome, protanopia, tritanopia));
                    gridButtons[row][col].setFont(new Font("Arial", Font.BOLD, 15));
                }
            }
        }

        // Update player info
        for (int i = 0; i < playerCount; i++) {
            playerInfoPanel.updateTurnsRemaining(i, turnsRemaining[i]);
        }

        // Update turn info
        updateTurnInfoLabel();

        // Reflect dice rolls if rolled
        if (hasRolled) {
            bottomPanel.removeAll();
            JButton rollButton = new JButton("Roll Dice");
            rollButton.setFont(new Font("Arial", Font.BOLD, 20));
            rollButton.setPreferredSize(new Dimension(150, 50));
            rollButton.addActionListener(new ActionListener() { // Add action listener to roll button
                @Override
                public void actionPerformed(ActionEvent e) {
                    rollDice(); // Roll dice
                }
            });
            bottomPanel.add(rollButton);

            JPanel dicePanel = new JPanel(new GridLayout(1, 6, 5, 0));
            dicePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            for (int roll : diceRolls) {
                JLabel diceLabel = new JLabel(String.valueOf(roll), SwingConstants.CENTER);
                diceLabel.setFont(new Font("Arial", Font.BOLD, 18));
                diceLabel.setPreferredSize(new Dimension(40, 40));
                diceLabel.setOpaque(true);
                diceLabel.setBackground(Color.WHITE);
                diceLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                dicePanel.add(diceLabel);
            }
            bottomPanel.add(dicePanel);

            JButton skipButton = new JButton("Skip Turn");
            skipButton.addActionListener(_ -> nextTurn());
            bottomPanel.add(skipButton);
        }

        frame.revalidate();
        frame.repaint();

    }

    private void loadGameState(GameState state) {
        this.playerCount = state.getPlayerCount();
        this.hasComputerPlayer = state.hasComputerPlayer();
        this.computerDifficulty = state.getComputerDifficulty();
        this.monochrome = state.isMonochrome();
        this.protanopia = state.isProtanopia();
        this.tritanopia = state.isTritanopia();
        this.stonePlacement = state.getStonePlacement();
        this.turnsRemaining = state.getTurnsRemaining();
        this.currentPlayer = state.getCurrentPlayer();
        this.turnsLeft = state.getTurnsLeft();
        this.diceRolls = state.getDiceRolls();
        this.hasRolled = state.hasRolled();
        this.displacedRow = state.getDisplacedRow();
        this.displacedPlayer = state.getDisplacedPlayer();
        this.validation = new Validation(diceRolls);
        CellManager.initializeBoard(boardCombinations, boardPoints, random);
    }

    private void saveGame() {
       
        GameState state = new GameState(playerCount, hasComputerPlayer, computerDifficulty, monochrome, protanopia,
                tritanopia, stonePlacement, turnsRemaining, currentPlayer, turnsLeft, diceRolls, hasRolled,
                displacedRow, displacedPlayer);
        try (FileOutputStream fileOut = new FileOutputStream("SavedKiviGame.ser");
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(state);
            JOptionPane.showMessageDialog(frame, "Game saved successfully!", "Save Game", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error saving game!", "Save Game", JOptionPane.ERROR_MESSAGE);
        }
    }
   

    // New method to reset the game
    private void resetGame() {
        // Reset game state
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                stonePlacement[row][col] = 0; // Clear all stones
                gridButtons[row][col].setText(boardCombinations[row][col]); // Reset text to initial combination
                gridButtons[row][col].setFont(new Font("Arial", Font.BOLD, 15)); // Reset font size to default 
                Color cellColor = CellManager.getCellColor(row, col, monochrome, protanopia, tritanopia);
                gridButtons[row][col].setBackground(cellColor); // Reset background colors
            }
        }
        
        // Reset stones for each player
        for (int i = 0; i < playerCount; i++) {
            turnsRemaining[i] = 10;
            playerInfoPanel.updateTurnsRemaining(i, 10);
        }
        
        turnsLeft = totalTurns; // Reset turns
        currentPlayer = 1; // Reset to first player
        hasRolled = false; // Reset roll state
        displacedRow = -1; // Clear displaced stone
        displacedPlayer = -1;
        diceRolls = new int[6]; // Reset dice rolls
        validation = new Validation(diceRolls); // Reinitialize validation
    
        // Reset bottom panel
        bottomPanel.removeAll();
        JButton rollButton = new JButton("Roll Dice");
        rollButton.setFont(new Font("Arial", Font.BOLD, 20)); // Increase font size
        rollButton.setPreferredSize(new Dimension(150, 50)); // Set a larger preferred size
        rollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rollDice();
            }
        });
        bottomPanel.add(rollButton);
    
        // Update turn info label
        updateTurnInfoLabel();
        
        // Refresh UI
        frame.revalidate();
        frame.repaint();
    }
    
    // Method to roll the dice
    private void rollDice() {
        if (!hasRolled) {
            hasRolled = true; // Set hasRolled to true
            for (int i = 0; i < 6; i++) {
                diceRolls[i] = random.nextInt(6) + 1; // Generate random dice rolls
            }

            validation = new Validation(diceRolls);
            
            bottomPanel.removeAll();

            // Add the Roll Dice button back
            JButton rollButton = new JButton("Roll Dice");
            rollButton.setFont(new Font("Arial", Font.BOLD, 20)); // Keep the larger font
            rollButton.setPreferredSize(new Dimension(150, 50)); // Keep the larger size
            rollButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    rollDice();
                }
            });
            bottomPanel.add(rollButton);

            // Create a panel for dice rolls in a single row
            JPanel dicePanel = new JPanel(new GridLayout(1, 6, 5, 0)); // 1 row, 6 columns, 5px horizontal gap
            dicePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
            for (int roll : diceRolls) {
                JLabel diceLabel = new JLabel(String.valueOf(roll), SwingConstants.CENTER);
                diceLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Larger font for dice
                diceLabel.setPreferredSize(new Dimension(40, 40)); // Square size for each die
                diceLabel.setOpaque(true); // Make it opaque to show background
                diceLabel.setBackground(Color.WHITE); // White background for blocks
                diceLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // Black border for block effect
                dicePanel.add(diceLabel);
            }

            // Add components to bottom panel
            bottomPanel.add(dicePanel);


            // new code end
            
            JButton skipButton = new JButton("Skip Turn");
            skipButton.addActionListener(_ -> {
                nextTurn();
            });
            bottomPanel.add(skipButton);
            
            frame.revalidate(); // Re validate the frame
            frame.repaint(); // Repaint the frame

        } else {
            JOptionPane.showMessageDialog(frame, "You have rolled your dice—now place your stone accordingly!", // Warning message if dice already rolled
                    "Place a Stone", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Method to handle cell click
    private void handleCellClick(int row, int col) {
        if (displacedRow != -1) {
            handleDisplacedStonePlacement(row, col); // Handle displaced stone placement
        } else {
            handleRegularClick(row, col); // Handle regular stone placement
        }
    }

    // Method to handle regular cell click
    private void handleRegularClick(int row, int col) {
        if (!hasRolled) {
            JOptionPane.showMessageDialog(frame, "Roll the dice first!"); // Prompt to roll dice
            return;
        }

        String combination = boardCombinations[row][col]; // Get combination for the cell
        boolean isOccupied = stonePlacement[row][col] != 0; // Check if the cell is occupied

        if (!validation.isValidPlacement(combination, isOccupied)) { // Validate placement
            if ((validation.isFiveOfAKind() || validation.isStraightOneToSix()) && isOccupied) {
                JOptionPane.showMessageDialog(frame, "This square is occupied! With five-of-a-kind or straight 1-6, you can only place on a free square.");
            } else {
                JOptionPane.showMessageDialog(frame, "Your dice do not satisfy the combination or the place is already occupied.");
            }
            return;
        }

        if (validation.isSixOfAKind() && isOccupied) { // Handle six of a kind
            displacedRow = row;
            displacedPlayer = stonePlacement[row][col];
            placeStone(row, col); // Replace the current stone
            JOptionPane.showMessageDialog(frame, "Stone replaced! Now place the displaced stone.");
            return;
        }

        if (displacedRow != -1) { // Handle displaced stone placement
            if (stonePlacement[row][col] == 0) { // Ensure it's an empty cell
                stonePlacement[row][col] = displacedPlayer; // Place displaced stone
                gridButtons[row][col].setBackground(Color.YELLOW);
                gridButtons[row][col].setText(PlayerInfoPanel.playerStones[displacedPlayer - 1]);
                gridButtons[row][col].setFont(new Font("Arial", Font.BOLD, 15));
                displacedRow = -1;
                displacedPlayer = -1;
                nextTurn();
            } else {
                JOptionPane.showMessageDialog(frame, "This square is already occupied!");
            }
            return;
        }

        placeStone(row, col); // Place the stone normally
        nextTurn();
    }

    // Places a stone on the board at the specified cell
    private void placeStone(int row, int col) {
        stonePlacement[row][col] = currentPlayer; // Update stone placement
        if (monochrome == true) {
            if (currentPlayer - 1 == 0) {
                gridButtons[row][col].setBackground(new Color(80, 80, 80));
            } else if (currentPlayer - 1 == 1) {
                gridButtons[row][col].setBackground(new Color(140, 140, 140));
            } else if (currentPlayer - 1 == 2) {
                gridButtons[row][col].setBackground(new Color(170, 170, 170));
            } else {
                gridButtons[row][col].setBackground(new Color(230, 230, 230));
            }
        } else if (protanopia == true) {
               if (currentPlayer - 1 == 0) {
                   gridButtons[row][col].setBackground(new Color(229, 226, 237));
               } else if (currentPlayer - 1 == 1) {
               gridButtons[row][col].setBackground(new Color(93, 143, 255));
               } else if (currentPlayer - 1 == 2) {
                gridButtons[row][col].setBackground(new Color(130, 127, 128));
            } else {
                gridButtons[row][col].setBackground(new Color(255, 234, 135));
            }
        } else if (tritanopia == true) {
            if (currentPlayer - 1 == 0) {
                gridButtons[row][col].setBackground(new Color(255, 182, 193));
            } else if (currentPlayer - 1 == 1) {
                gridButtons[row][col].setBackground(new Color(97, 210, 250));
            } else if (currentPlayer - 1 == 2) {
                gridButtons[row][col].setBackground(new Color(200, 200, 200));
            } else {
                gridButtons[row][col].setBackground(new Color(128, 128, 128));
            }
        } else {
            if (currentPlayer - 1 == 0) {
                gridButtons[row][col].setBackground(new Color(217, 235, 250));
            } else if (currentPlayer - 1 == 1) {
                gridButtons[row][col].setBackground(new Color(128, 180, 210));
            } else if (currentPlayer - 1 == 2) {
                gridButtons[row][col].setBackground(new Color(3, 106, 140));
            } else {
                gridButtons[row][col].setBackground(new Color(0, 77, 172));
            }
        }
        gridButtons[row][col].setText(PlayerInfoPanel.playerStones[currentPlayer - 1]); // Set stone symbol
        gridButtons[row][col].setFont(new Font("Arial", Font.BOLD, 60)); // Set font size for visibility
    }

    // Prepares the game for the next player's turn
    private void nextTurn() {
        hasRolled = false; // Reset roll status
        bottomPanel.removeAll(); // Clear bottom panel
        JButton rollButton = new JButton("Roll Dice"); // Create roll button
        rollButton.setFont(new Font("Arial", Font.BOLD, 20)); // Keep the larger font
        rollButton.setPreferredSize(new Dimension(150, 50)); // Keep the larger size
        rollButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rollDice(); // Roll dice action
            }
        });
        bottomPanel.add(rollButton); // Add roll button to panel

        if (displacedRow == -1) { // If no stone was displaced
            turnsRemaining[currentPlayer - 1]--; // Decrement stones remaining for current player
            playerInfoPanel.updateTurnsRemaining(currentPlayer - 1, turnsRemaining[currentPlayer - 1]); // Update UI
        }

        boolean gameOver = true; // Assume game is over
        for (int stones : turnsRemaining) { // Check if any player still has stones
            if (stones > 0) {
                gameOver = false; // Game continues
                break;
            }
        }

        if (gameOver) { // If game is over
            endGame(); // End the game
            return;
        }

        currentPlayer = (currentPlayer % playerCount) + 1; // Switch to next player
        updateTurnInfoLabel(); // Update turn info label

        if (hasComputerPlayer && currentPlayer == playerCount) { // If it's computer's turn
            // Create a timer that delays the computer's turn by 0.1 second for better UX
            Timer timer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleComputerTurn();
                }
            });
            timer.setRepeats(false); // Ensures the timer only fires once
            timer.start(); // Starts the timer
        }

        frame.revalidate(); // Re validate frame
        frame.repaint(); // Repaint frame
    }

    // Updates the turn information label
    private void updateTurnInfoLabel() {
        turnInfoLabel.setText("Player " + currentPlayer + "'s Turn (" + PlayerInfoPanel.playerStones[currentPlayer - 1] + ") - Turns Left: " + turnsRemaining[currentPlayer - 1]);
    }

    // Ends the game and displays the results
    private void endGame() {
        Scoring scoring = new Scoring(stonePlacement, boardPoints, playerCount); // Calculate scores
        int[] scores = scoring.calculateScores();

        int maxScore = 0; // Find the highest score
        for (int score : scores) {
            maxScore = Math.max(maxScore, score);
        }

        StringBuilder winners = new StringBuilder(); // Build winners string
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] == maxScore) {
                if (winners.length() > 0) {
                    winners.append(" and ");
                }
                winners.append("Player ").append(i + 1);
            }
        }

        StringBuilder resultMessage = new StringBuilder("Game Over!\n\nFinal Scores:\n"); // Build result message
        for (int i = 0; i < playerCount; i++) {
            resultMessage.append("Player ").append(i + 1).append(": ").append(scores[i]).append(" points\n");
        }
        resultMessage.append("\nWinner(s): ").append(winners);
        
        // Use JOptionPane with custom options
        Object[] options = {"Exit", "New Game"};
        int choice = JOptionPane.showOptionDialog(
            frame,
            resultMessage.toString(),
            "Game Results",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[1]
        );

        // Handle the user's choice
        if (choice == 0) { // Exit
            System.exit(0);
        } else if (choice == 1) { // New Game
            startNewGame();
        }
    }

    private void startNewGame() {
        // Clear the current frame content
        frame.getContentPane().removeAll();
        // Show the MainMenu in the existing frame
        MainMenu mainMenu = new MainMenu(frame);
        frame.add(mainMenu.getPanel());
        frame.revalidate();
        frame.repaint();
    }
        
    // Handles displaced stone placement
    private void handleDisplacedStonePlacement(int row, int col) {
        if (displacedRow == -1) return; // If no stone was displaced, do nothing

        if (stonePlacement[row][col] != 0) { // If cell is occupied
            JOptionPane.showMessageDialog(frame, "This square is already occupied!");
            return;
        }

        stonePlacement[row][col] = displacedPlayer; // Place displaced stone
        gridButtons[row][col].setBackground(Color.YELLOW);
        gridButtons[row][col].setText(PlayerInfoPanel.playerStones[displacedPlayer - 1]);
        gridButtons[row][col].setFont(new Font("Arial", Font.BOLD, 20));
        displacedRow = -1; // Reset displaced stone info
        displacedPlayer = -1;
        nextTurn(); // Proceed to next turn
    }

 // Handles the computer's turn
    private void handleComputerTurn() {
        rollDice(); // Roll dice for computer

        // Add a delay to allow the user to see the dice roll before the computer makes a move
        Timer delayTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (displacedRow != -1) { // If there's a displaced stone
                    int[] move = computerPlayer.placeDisplacedStone(stonePlacement); 
                    if (move != null) {
                        handleDisplacedStonePlacement(move[0], move[1]); 
                    }
                    return;
                }

                int[] move = computerPlayer.determineMove(boardCombinations, stonePlacement, boardPoints, validation); 
                if (move != null) {
                    if (validation.isSixOfAKind() && stonePlacement[move[0]][move[1]] != 0) { 
                        displacedRow = move[0];
                        displacedPlayer = stonePlacement[move[0]][move[1]];
                        placeStone(move[0], move[1]); 

                        // Delay displaced stone placement
                        Timer displacedTimer = new Timer(1500, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                int[] displacedMove = computerPlayer.placeDisplacedStone(stonePlacement);
                                if (displacedMove != null) {
                                    handleDisplacedStonePlacement(displacedMove[0], displacedMove[1]);
                                }
                            }
                        });
                        displacedTimer.setRepeats(false);
                        displacedTimer.start();
                    } else {
                        placeStone(move[0], move[1]); 
                        // Delay next turn to show the stone placement
                        Timer nextTurnTimer = new Timer(1000, _ -> nextTurn());
                        nextTurnTimer.setRepeats(false);
                        nextTurnTimer.start();
                    }
                } else {
                    // Delay next turn to show message
                    Timer messageTimer = new Timer(500, _ -> {
                        JOptionPane.showMessageDialog(frame, "Computer cannot make a valid move with current dice roll.", "Computer's Turn", JOptionPane.INFORMATION_MESSAGE); 
                        nextTurn(); 
                    });
                    messageTimer.setRepeats(false);
                    messageTimer.start();
                }
            }
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }
}