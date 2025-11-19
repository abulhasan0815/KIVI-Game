import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Computer player logic for the Kivi game
 * 
 * @author Group 4
 */
public class ComputerPlayer {
    private static final int GRID_SIZE = 7; // Size of the game grid
    private Random random = new Random(); // Random number generator
    private String difficulty; // "Easy" or "Hard"
    private static final int MAX_DEPTH = 2; // Depth for minimax algorithm
    
    // Constructor initializes the computer player with a difficulty level
    public ComputerPlayer(String difficulty) {
        this.difficulty = difficulty;
    }
    
    // Determines the best move for the computer player
    public int[] determineMove(String[][] boardCombinations, int[][] stonePlacement, 
                              int[][] boardPoints, Validation validation) {
        List<int[]> validMoves = new ArrayList<>(); // List to store valid moves [row, col, points]
        
        // Special case: check for six of a kind first (can place anywhere)
        if (validation.isSixOfAKind()) {
            return handleSixOfAKindMove(stonePlacement, boardPoints);
        }
        
        // Find all valid moves based on current dice roll
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                boolean isOccupied = stonePlacement[row][col] != 0; // Check if the cell is occupied
                String combination = boardCombinations[row][col]; // Get the combination for the cell
                
                // Check if placement is valid
                if (validation.isValidPlacement(combination, isOccupied)) {
                    int points = boardPoints[row][col]; // Get the points for the cell
                    int[] move = {row, col, points}; // Create a move array [row, col, points]
                    validMoves.add(move); // Add the move to the list of valid moves
                }
            }
        }
        
        // If no valid moves found
        if (validMoves.isEmpty()) {
            return null; // Return null if no valid moves
        }
        
        // Choose move based on difficulty
        if (difficulty.equals("Hard")) {
            return chooseHardMove(validMoves, stonePlacement, boardPoints);
        } else {
            return chooseEasyMove(validMoves);
        }
    }
    
    // Easy mode: Simply selects random valid moves
    private int[] chooseEasyMove(List<int[]> validMoves) {
        // Easy mode just randomly selects from valid moves with minimal strategy
        
        if (validMoves.size() == 1) {
            // If only one valid move, return it
            int[] selectedMove = validMoves.get(0);
            return new int[] {selectedMove[0], selectedMove[1]};
        }
        
        // Very simple strategy: 15% chance to choose the highest point move
        if (random.nextInt(100) < 15) {
            // Find the move with the highest points
            int[] bestMove = validMoves.get(0);
            for (int[] move : validMoves) {
                if (move[2] > bestMove[2]) {
                    bestMove = move;
                }
            }
            return new int[] {bestMove[0], bestMove[1]};
        }
        
        // Otherwise, randomly select a move
        int[] selectedMove = validMoves.get(random.nextInt(validMoves.size()));
        return new int[] {selectedMove[0], selectedMove[1]};
    }
    
    // Hard mode: Uses minimax strategy to select the best move
    private int[] chooseHardMove(List<int[]> validMoves, int[][] stonePlacement, int[][] boardPoints) {
        int[] bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        
        // Create a copy of the stone placement to simulate moves
        int[][] tempStonePlacement = new int[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                tempStonePlacement[i][j] = stonePlacement[i][j];
            }
        }
        
        // Evaluate each valid move using minimax
        for (int[] move : validMoves) {
            int row = move[0];
            int col = move[1];
            int points = move[2];
            
            // Make the move
            tempStonePlacement[row][col] = 1; // 1 represents computer's stone
            
            // Calculate score using minimax
            int score = minimax(tempStonePlacement, boardPoints, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            
            // Add immediate points to the score
            score += points;
            
            // Undo the move
            tempStonePlacement[row][col] = stonePlacement[row][col];
            
            // Update best move if this move has a higher score
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        
        // If we found a best move, return it
        if (bestMove != null) {
            return new int[] {bestMove[0], bestMove[1]};
        }
        
        // Fallback: return the first valid move
        int[] selectedMove = validMoves.get(0);
        return new int[] {selectedMove[0], selectedMove[1]};
    }
    
    // Minimax algorithm with alpha-beta pruning
    private int minimax(int[][] stonePlacement, int[][] boardPoints, int depth, boolean isMaximizing, int alpha, int beta) {
        // Terminal condition: reached maximum depth
        if (depth >= MAX_DEPTH) {
            return evaluateBoard(stonePlacement, boardPoints);
        }
        
        // Find all available spaces
        List<int[]> availableMoves = getAvailableMoves(stonePlacement, boardPoints);
        
        // Terminal condition: no more available moves
        if (availableMoves.isEmpty()) {
            return evaluateBoard(stonePlacement, boardPoints);
        }
        
        if (isMaximizing) {
            // Computer's turn (maximizing)
            int maxScore = Integer.MIN_VALUE;
            for (int[] move : availableMoves) {
                int row = move[0];
                int col = move[1];
                
                // Make the move
                stonePlacement[row][col] = 1; // 1 for computer
                
                // Recursively find the best score
                int score = minimax(stonePlacement, boardPoints, depth + 1, false, alpha, beta);
                
                // Undo the move
                stonePlacement[row][col] = 0;
                
                maxScore = Math.max(maxScore, score);
                alpha = Math.max(alpha, score);
                
                // Alpha-beta pruning
                if (beta <= alpha) {
                    break;
                }
            }
            return maxScore;
        } else {
            // Opponent's turn (minimizing)
            int minScore = Integer.MAX_VALUE;
            for (int[] move : availableMoves) {
                int row = move[0];
                int col = move[1];
                
                // Make the move
                stonePlacement[row][col] = 2; // 2 for opponent
                
                // Recursively find the best score
                int score = minimax(stonePlacement, boardPoints, depth + 1, true, alpha, beta);
                
                // Undo the move
                stonePlacement[row][col] = 0;
                
                minScore = Math.min(minScore, score);
                beta = Math.min(beta, score);
                
                // Alpha-beta pruning
                if (beta <= alpha) {
                    break;
                }
            }
            return minScore;
        }
    }
    
    // Get all available moves for minimax
    private List<int[]> getAvailableMoves(int[][] stonePlacement, int[][] boardPoints) {
        List<int[]> availableMoves = new ArrayList<>();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (stonePlacement[row][col] == 0) { // Empty space
                    availableMoves.add(new int[] {row, col, boardPoints[row][col]});
                }
            }
        }
        return availableMoves;
    }
    
    // Evaluate the board state for minimax
    private int evaluateBoard(int[][] stonePlacement, int[][] boardPoints) {
        int score = 0;
        
        // Count points for computer (1) and opponent (2)
        int computerPoints = 0;
        int opponentPoints = 0;
        
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (stonePlacement[row][col] == 1) { // Computer's stone
                    computerPoints += boardPoints[row][col];
                    
                    // Add bonus for strategic positions
                    score += calculatePositionalValue(row, col);
                } else if (stonePlacement[row][col] == 2) { // Opponent's stone
                    opponentPoints += boardPoints[row][col];
                }
            }
        }
        
        // Consider the point difference as the main score
        score += (computerPoints - opponentPoints);
        
        // Consider potential future moves
        score += evaluatePotentialMoves(stonePlacement, boardPoints);
        
        return score;
    }
    
    // Calculate positional value based on position on the board
    private int calculatePositionalValue(int row, int col) {
        // Prefer central positions over edge positions
        int distanceFromCenter = Math.abs(row - GRID_SIZE/2) + Math.abs(col - GRID_SIZE/2);
        return (GRID_SIZE - distanceFromCenter);
    }
    
    // Evaluate potential future moves
    private int evaluatePotentialMoves(int[][] stonePlacement, int[][] boardPoints) {
        int score = 0;
        
        // Check for high-value empty cells that are adjacent to occupied cells
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Up, down, left, right
        
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (stonePlacement[row][col] == 0) { // Empty cell
                    int cellValue = boardPoints[row][col];
                    
                    // Check if this empty cell is adjacent to an occupied cell
                    boolean isAdjacent = false;
                    for (int[] dir : directions) {
                        int newRow = row + dir[0];
                        int newCol = col + dir[1];
                        
                        if (newRow >= 0 && newRow < GRID_SIZE && newCol >= 0 && newCol < GRID_SIZE) {
                            if (stonePlacement[newRow][newCol] != 0) {
                                isAdjacent = true;
                                break;
                            }
                        }
                    }
                    
                    // High-value empty cells that are adjacent to occupied cells are valuable
                    if (isAdjacent && cellValue >= 3) {
                        score += cellValue;
                    }
                }
            }
        }
        
        return score;
    }
    
    // Handles special case moves like six-of-a-kind
    private int[] handleSixOfAKindMove(int[][] stonePlacement, int[][] boardPoints) {
        if (difficulty.equals("Hard")) {
            return handleHardSixOfAKind(stonePlacement, boardPoints);
        } else {
            return handleEasySixOfAKind(stonePlacement, boardPoints);
        }
    }
    
    // Easy strategy for six-of-a-kind: completely random
    private int[] handleEasySixOfAKind(int[][] stonePlacement, int[][] boardPoints) {
        // Just pick a random cell on the board
        int row = random.nextInt(GRID_SIZE);
        int col = random.nextInt(GRID_SIZE);
        return new int[] {row, col};
    }
    
    // Hard strategy for six-of-a-kind: strategic choice
    private int[] handleHardSixOfAKind(int[][] stonePlacement, int[][] boardPoints) {
        // For six-of-a-kind, the hard player should prioritize:
        // 1. Highest point cells
        // 2. Already occupied high-value cells (to block opponent)
        // 3. Strategic positions
        
        int bestRow = 0;
        int bestCol = 0;
        int bestScore = Integer.MIN_VALUE;
        
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int score = boardPoints[row][col] * 3; // Points are most important
                
                // Add bonus for already occupied cells (strategic to block opponent)
                if (stonePlacement[row][col] != 0) {
                    score += boardPoints[row][col] * 2;
                }
                
                // Add bonus for positional value
                score += calculatePositionalValue(row, col);
                
                if (score > bestScore) {
                    bestScore = score;
                    bestRow = row;
                    bestCol = col;
                }
            }
        }
        
        return new int[] {bestRow, bestCol};
    }
    
    // Handles the case of displaced stone placement
    public int[] placeDisplacedStone(int[][] stonePlacement) {
        List<int[]> emptySpaces = new ArrayList<>(); // List to store empty spaces
        
        // Find all empty spaces
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (stonePlacement[row][col] == 0) {
                    emptySpaces.add(new int[] {row, col});
                }
            }
        }
        
        if (!emptySpaces.isEmpty()) {
            if (difficulty.equals("Hard")) {
                // For hard mode, use minimax to find the best placement
                int bestRow = 0;
                int bestCol = 0;
                int bestScore = Integer.MIN_VALUE;
                
                // Create a copy of the stone placement to simulate moves
                int[][] tempStonePlacement = new int[GRID_SIZE][GRID_SIZE];
                for (int i = 0; i < GRID_SIZE; i++) {
                    for (int j = 0; j < GRID_SIZE; j++) {
                        tempStonePlacement[i][j] = stonePlacement[i][j];
                    }
                }
                
                // Evaluate each empty space
                for (int[] space : emptySpaces) {
                    int row = space[0];
                    int col = space[1];
                    
                    // Make the move
                    tempStonePlacement[row][col] = 1; // 1 represents computer's stone
                    
                    // Calculate score using positional value (simplified for displaced stones)
                    int score = calculatePositionalValue(row, col);
                    
                    // Undo the move
                    tempStonePlacement[row][col] = 0;
                    
                    // Update best move if this move has a higher score
                    if (score > bestScore) {
                        bestScore = score;
                        bestRow = row;
                        bestCol = col;
                    }
                }
                
                return new int[] {bestRow, bestCol};
            } else {
                // For easy mode, just select a random empty space
                int[] selectedSpace = emptySpaces.get(random.nextInt(emptySpaces.size()));
                return new int[] {selectedSpace[0], selectedSpace[1]};
            }
        }
        
        // Should never get here if game is working correctly
        return null;
    }
}