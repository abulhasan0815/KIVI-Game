import java.io.Serializable;

/**
 * Holds the game state for saving and loading
 *
 * @author Group 4
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L; // For serialization compatibility

    private int playerCount; // Number of players in the game
    private boolean hasComputerPlayer; // Whether there is a computer player
    private String computerDifficulty; // Difficulty level of the computer player
    private boolean monochrome; // Whether the game is in monochrome mode
    private boolean protanopia; // Whether the game is in protanopia mode
    private boolean tritanopia; // Whether the game is in tritanopia mode
    private int[][] stonePlacement; // Grid storing stone placements
    private int[] turnsRemaining; // Array storing remaining turns for each player
    private int currentPlayer; // Current player's turn
    private int turnsLeft; // Total remaining turns in the game
    private int[] diceRolls; // Array storing the current dice rolls
    private boolean hasRolled; // Whether the dice have been rolled
    private int displacedRow; // Row of the displaced stone
    private int displacedPlayer; // Player whose stone was displaced

    // Constructor initializes the game state
    public GameState(int playerCount, boolean hasComputerPlayer, String computerDifficulty, boolean monochrome,
                     boolean protanopia, boolean tritanopia, int[][] stonePlacement, int[] turnsRemaining,
                     int currentPlayer, int turnsLeft, int[] diceRolls, boolean hasRolled, int displacedRow,
                     int displacedPlayer) {
        this.playerCount = playerCount;
        this.hasComputerPlayer = hasComputerPlayer;
        this.computerDifficulty = computerDifficulty;
        this.monochrome = monochrome;
        this.protanopia = protanopia;
        this.tritanopia = tritanopia;
        this.stonePlacement = deepCopy(stonePlacement); // Deep copy to avoid reference issues
        this.turnsRemaining = turnsRemaining.clone();
        this.currentPlayer = currentPlayer;
        this.turnsLeft = turnsLeft;
        this.diceRolls = diceRolls.clone();
        this.hasRolled = hasRolled;
        this.displacedRow = displacedRow;
        this.displacedPlayer = displacedPlayer;
    }

    // Deep copy method for 2D array to avoid reference issues
    private int[][] deepCopy(int[][] original) {
        if (original == null) return null;
        int[][] result = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = original[i].clone();
        }
        return result;
    }

    // Getters to access the game state variables
    public int getPlayerCount() { return playerCount; }
    public boolean hasComputerPlayer() { return hasComputerPlayer; }
    public String getComputerDifficulty() { return computerDifficulty; }
    public boolean isMonochrome() { return monochrome; }
    public boolean isProtanopia() { return protanopia; }
    public boolean isTritanopia() { return tritanopia; }
    public int[][] getStonePlacement() { return stonePlacement; }
    public int[] getTurnsRemaining() { return turnsRemaining; }
    public int getCurrentPlayer() { return currentPlayer; }
    public int getTurnsLeft() { return turnsLeft; }
    public int[] getDiceRolls() { return diceRolls; }
    public boolean hasRolled() { return hasRolled; }
    public int getDisplacedRow() { return displacedRow; }
    public int getDisplacedPlayer() { return displacedPlayer; }
}