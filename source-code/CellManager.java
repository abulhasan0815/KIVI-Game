import java.util.Random;
import java.awt.Color;

/**
 * Manages cell colors and text
 *
 * @author Group 4
 * 
 */

public class CellManager {
    private static final String[] possibleCombinations = {
        "AA BB", "AAA", "ABCD", "AAA BB", "AAAA", "ABCDE", "= 2,4,6", "= 1,3,5", "<= 12", ">= 30", "AA BB CC", "AAA BBB", "AAAA BB"
    }; // Possible combinations for the game
    private static final int[] combinationPoints = {1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3}; // Points for each combination

    // Method to get cell background color
    public static Color getCellColor(int row, int col, boolean monochrome, boolean protanopia, boolean tritanopia) {
        // Logic for color determination
        if (row == 0 && col == 0 || row == 3 && col == 5 || row == 4 && col == 6 || row == 6 && col == 3 || row == 0 && col == 3 || 
            row == 0 && col == 6 || row == 1 && col == 2 || row == 4 && col == 0 || row == 1 && col == 4 || row == 2 && col == 0 || 
            row == 5 && col == 2 || row == 6 && col == 0) {
            return Color.WHITE;
        }
        else if (row == 2 && col == 1 || row == 4 && col == 4 || row == 6 && col == 2 || row == 6 && col == 5 || row == 0 && col == 1 || 
            row == 2 && col == 3 || row == 3 && col == 6 || row == 4 && col == 1 || row == 0 && col == 5 || row == 1 && col == 0 || 
            row == 3 && col == 2 || row == 0 && col == 4 || row == 2 && col == 5 || row == 4 && col == 3 || row == 5 && col == 0 || 
            row == 6 && col == 4 || row == 0 && col == 2 || row == 3 && col == 0 || row == 3 && col == 4 || row == 5 && col == 6 ||
            row == 1 && col == 6 || row == 2 && col == 2 || row == 4 && col == 5 || row == 6 && col == 1) {
            if (monochrome) {
                return new Color(200, 200, 200);
            } else if (protanopia) {
                return new Color(255, 215, 0); // Gold for protanopia
            } else if (tritanopia) {
                return new Color(255, 0, 0); // Red for tritanopia
            } else {
                return new Color(255, 176, 203); // Original pink
            }
        }
        else if (row == 1 && col == 3 || row == 4 && col == 2 || row == 5 && col == 5 || row == 1 && col == 5 || row == 3 && col == 3 || 
            row == 5 && col == 1 || row == 1 && col == 1 || row == 2 && col == 4 || row == 5 && col == 3) {
            if (monochrome) {
                return new Color(120, 120, 120);
            } else if (protanopia) {
                return new Color(0, 0, 255); // Blue for protanopia
            } else if (tritanopia) {
                return new Color(0, 128, 128); // Teal for tritanopia
            } else {
                return new Color(213, 66, 115); // Original red
            }
        }
        else {
            return Color.WHITE;
        }
    }

    // Method to initialize the board with combinations and points
    public static void initializeBoard(String[][] boardCombinations, int[][] boardPoints, Random random) {
        for (int row = 0; row < boardCombinations.length; row++) {
            for (int col = 0; col < boardCombinations[row].length; col++) {
                // Assign combinations based on cell position
                if (row == 0 && col == 0 || row == 3 && col == 5 || row == 4 && col == 6 || row == 6 && col == 3)
                    boardCombinations[row][col] = possibleCombinations[0]; // AA BB
                else if (row == 0 && col == 3 || row == 0 && col == 6 || row == 1 && col == 2 || row == 4 && col == 0)
                    boardCombinations[row][col] = possibleCombinations[1]; // AAA
                else if (row == 1 && col == 4 || row == 2 && col == 0 || row == 5 && col == 2 || row == 6 && col == 0)
                    boardCombinations[row][col] = possibleCombinations[2]; // ABCD
                else if (row == 2 && col == 1 || row == 4 && col == 4 || row == 6 && col == 2 || row == 6 && col == 5)
                    boardCombinations[row][col] = possibleCombinations[4]; // AAAA
                else if (row == 0 && col == 1 || row == 2 && col == 3 || row == 3 && col == 6 || row == 4 && col == 1)
                    boardCombinations[row][col] = possibleCombinations[5]; // ABCDE
                else if (row == 0 && col == 5 || row == 1 && col == 0 || row == 3 && col == 2)
                    boardCombinations[row][col] = possibleCombinations[6]; // = 2,4,6
                else if (row == 0 && col == 4 || row == 2 && col == 5 || row == 4 && col == 3 || row == 5 && col == 0 || row == 6 && col == 4)
                    boardCombinations[row][col] = possibleCombinations[7]; // = 1,3,5
                else if (row == 0 && col == 2 || row == 3 && col == 0 || row == 3 && col == 4 || row == 5 && col == 6)
                    boardCombinations[row][col] = possibleCombinations[8]; // <= 12
                else if (row == 1 && col == 6 || row == 2 && col == 2 || row == 4 && col == 5 || row == 6 && col == 1)
                    boardCombinations[row][col] = possibleCombinations[9]; // >= 30
                else if (row == 1 && col == 3 || row == 4 && col == 2 || row == 5 && col == 5)
                    boardCombinations[row][col] = possibleCombinations[10]; // AA BB CC
                else if (row == 1 && col == 5 || row == 3 && col == 3 || row == 5 && col == 1)
                    boardCombinations[row][col] = possibleCombinations[11]; // AAA BBB
                else if (row == 1 && col == 1 || row == 2 && col == 4 || row == 5 && col == 3)
                    boardCombinations[row][col] = possibleCombinations[12]; // AAAA BB
                else
                    boardCombinations[row][col] = possibleCombinations[3]; // AAA BB
                boardPoints[row][col] = combinationPoints[random.nextInt(combinationPoints.length)]; // Assign points randomly
            }
        }
    }
}