/**
 * Calculates final scores for the Kivi game
 *
 * @author Group 4
 */
public class Scoring {
    private int[][] stonePlacement; // The grid showing which player's stone is in each cell
    private int[][] boardPoints;    // Points for each cell (1, 2, or 3)
    private int playerCount;        // Number of players
    private static final int GRID_SIZE = 7;

    // Constructor for Scoring class
    public Scoring(int[][] stonePlacement, int[][] boardPoints, int playerCount) {
        this.stonePlacement = stonePlacement;
        this.boardPoints = boardPoints;
        this.playerCount = playerCount;
    }

    // Calculates scores for all players
    public int[] calculateScores() {
        int[] scores = new int[playerCount];

        // Calculate scores for horizontal rows
        calculateHorizontalRows(scores);

        // Calculate scores for vertical rows
        calculateVerticalRows(scores);

        return scores;
    }

    // Calculates scores for horizontal rows
    private void calculateHorizontalRows(int[] scores) {
        // For each row in the grid
        for (int row = 0; row < GRID_SIZE; row++) {
            int currentPlayer = -1;
            int rowLength = 0;
            int rowPoints = 0;

            // For each column in the row
            for (int col = 0; col < GRID_SIZE; col++) {
                int cellPlayer = stonePlacement[row][col];

                if (cellPlayer != 0 && cellPlayer == currentPlayer) {
                    // Continue the current row
                    rowLength++;
                    rowPoints += boardPoints[row][col];
                } else {
                    // End the current row if it exists
                    if (rowLength > 0) {
                        // Score = sum of points × length of row
                        scores[currentPlayer - 1] += rowPoints * rowLength;
                    }

                    // Start a new row if there's a stone
                    if (cellPlayer != 0) {
                        currentPlayer = cellPlayer;
                        rowLength = 1;
                        rowPoints = boardPoints[row][col];
                    } else {
                        currentPlayer = -1;
                        rowLength = 0;
                        rowPoints = 0;
                    }
                }
            }

            // Don't forget to score the last row if it exists
            if (rowLength > 0) {
                scores[currentPlayer - 1] += rowPoints * rowLength;
            }
        }
    }

    // Calculates scores for vertical rows
    private void calculateVerticalRows(int[] scores) {
        // For each column in the grid
        for (int col = 0; col < GRID_SIZE; col++) {
            int currentPlayer = -1;
            int colLength = 0;
            int colPoints = 0;

            // For each row in the column
            for (int row = 0; row < GRID_SIZE; row++) {
                int cellPlayer = stonePlacement[row][col];

                if (cellPlayer != 0 && cellPlayer == currentPlayer) {
                    // Continue the current column
                    colLength++;
                    colPoints += boardPoints[row][col];
                } else {
                    // End the current column if it exists
                    if (colLength > 0) {
                        // Score = sum of points × length of column
                        scores[currentPlayer - 1] += colPoints * colLength;
                    }

                    // Start a new column if there's a stone
                    if (cellPlayer != 0) {
                        currentPlayer = cellPlayer;
                        colLength = 1;
                        colPoints = boardPoints[row][col];
                    } else {
                        currentPlayer = -1;
                        colLength = 0;
                        colPoints = 0;
                    }
                }
            }

            // Don't forget to score the last column if it exists
            if (colLength > 0) {
                scores[currentPlayer - 1] += colPoints * colLength;
            }
        }
    }

    // Gets detailed scoring information for display
    public String getDetailedScoring() {
        StringBuilder details = new StringBuilder();
        int[][] horizontalScores = new int[playerCount][GRID_SIZE];
        int[][] verticalScores = new int[playerCount][GRID_SIZE];

        // Calculate and store detailed horizontal scores
        calculateDetailedHorizontalScores(horizontalScores);

        // Calculate and store detailed vertical scores
        calculateDetailedVerticalScores(verticalScores);

        // Build the detailed scoring string
        for (int player = 0; player < playerCount; player++) {
            details.append("Player ").append(player + 1).append(" scoring:\n");

            // Horizontal rows
            details.append("  Horizontal rows: ");
            int totalHorizontal = 0;
            for (int row = 0; row < GRID_SIZE; row++) {
                if (horizontalScores[player][row] > 0) {
                    details.append("Row ").append(row + 1).append(": ").append(horizontalScores[player][row]).append(", ");
                    totalHorizontal += horizontalScores[player][row];
                }
            }
            details.append("Total: ").append(totalHorizontal).append("\n");

            // Vertical rows
            details.append("  Vertical rows: ");
            int totalVertical = 0;
            for (int col = 0; col < GRID_SIZE; col++) {
                if (verticalScores[player][col] > 0) {
                    details.append("Col ").append(col + 1).append(": ").append(verticalScores[player][col]).append(", ");
                    totalVertical += verticalScores[player][col];
                }
            }
            details.append("Total: ").append(totalVertical).append("\n");

            details.append("  Total Score: ").append(totalHorizontal + totalVertical).append("\n\n");
        }

        return details.toString();
    }

    // Calculates detailed scores for horizontal rows
    private void calculateDetailedHorizontalScores(int[][] horizontalScores) {
        // For each row in the grid
        for (int row = 0; row < GRID_SIZE; row++) {
            int currentPlayer = -1;
            int rowLength = 0;
            int rowPoints = 0;

            // For each column in the row
            for (int col = 0; col < GRID_SIZE; col++) {
                int cellPlayer = stonePlacement[row][col];

                if (cellPlayer != 0 && cellPlayer == currentPlayer) {
                    // Continue the current row
                    rowLength++;
                    rowPoints += boardPoints[row][col];
                } else {
                    // End the current row if it exists
                    if (rowLength > 0) {
                        // Score = sum of points × length of row
                        horizontalScores[currentPlayer - 1][row] += rowPoints * rowLength;
                    }

                    // Start a new row if there's a stone
                    if (cellPlayer != 0) {
                        currentPlayer = cellPlayer;
                        rowLength = 1;
                        rowPoints = boardPoints[row][col];
                    } else {
                        currentPlayer = -1;
                        rowLength = 0;
                        rowPoints = 0;
                    }
                }
            }

            // Don't forget to score the last row if it exists
            if (rowLength > 0) {
                horizontalScores[currentPlayer - 1][row] += rowPoints * rowLength;
            }
        }
    }

    // Calculates detailed scores for vertical rows
    private void calculateDetailedVerticalScores(int[][] verticalScores) {
        // For each column in the grid
        for (int col = 0; col < GRID_SIZE; col++) {
            int currentPlayer = -1;
            int colLength = 0;
            int colPoints = 0;

            // For each row in the column
            for (int row = 0; row < GRID_SIZE; row++) {
                int cellPlayer = stonePlacement[row][col];

                if (cellPlayer != 0 && cellPlayer == currentPlayer) {
                    // Continue the current column
                    colLength++;
                    colPoints += boardPoints[row][col];
                } else {
                    // End the current column if it exists
                    if (colLength > 0) {
                        // Score = sum of points × length of column
                        verticalScores[currentPlayer - 1][col] += colPoints * colLength;
                    }

                    // Start a new column if there's a stone
                    if (cellPlayer != 0) {
                        currentPlayer = cellPlayer;
                        colLength = 1;
                        colPoints = boardPoints[row][col];
                    } else {
                        currentPlayer = -1;
                        colLength = 0;
                        colPoints = 0;
                    }
                }
            }

            // Don't forget to score the last column if it exists
            if (colLength > 0) {
                verticalScores[currentPlayer - 1][col] += colPoints * colLength;
            }
        }
    }
}