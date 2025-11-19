import java.util.ArrayList;
import java.util.Arrays;

/**
 * Checks for valid placements of stones
 *
 * @author Group 4
 * 
 */
public class Validation {
    private int[] diceRolls;

    // Constructor initializes the validation instance with dice rolls
    public Validation(int[] diceRolls) {
        this.diceRolls = diceRolls;
    }

    // Checks if the dice satisfy the given combination or if it's a special roll
    public boolean isValidPlacement(String combination, boolean isOccupied) {
        if (isSixOfAKind()) {
            return true; // Can place in any cell, including occupied ones.
        } else if (isFiveOfAKind() || isStraightOneToSix()) {
            return !isOccupied; // Can only place in unoccupied cells.
        } else if (isOccupied) {
            return false; // Occupied cells are invalid for other combinations.
        }

        int[] sortedDice = diceRolls.clone();
        Arrays.sort(sortedDice);

        switch (combination) {
            case "AA BB": return checkTwoPairs(sortedDice);
            case "AAA": return checkThreeOfAKind(sortedDice);
            case "ABCD": return checkLittleStraight(sortedDice);
            case "AAA BB": return checkFullHouse(sortedDice);
            case "AAAA": return checkFourOfAKind(sortedDice);
            case "ABCDE": return checkLargeStraight(sortedDice);
            case "= 2,4,6": return checkAllEven(diceRolls);
            case "= 1,3,5": return checkAllOdd(diceRolls);
            case "<= 12": return checkSumLessThanOrEqual(diceRolls, 12);
            case ">= 30": return checkSumGreaterThanOrEqual(diceRolls, 30);
            case "AA BB CC": return checkThreePairs(sortedDice);
            case "AAA BBB": return checkTwoThreeOfAKind(sortedDice);
            case "AAAA BB": return checkFourOfAKindAndPair(sortedDice);
            default: return false;
        }
    }

    // Checks if at least five dice are the same
    public boolean isFiveOfAKind() {
        for (int i = 1; i <= 6; i++) { // Loop through numbers 1 to 6
            int count = 0; // Reset count for each number
            for (int die : diceRolls) { // Check each roll
                if (die == i) {
                    count++; // Increment count for this number
                    if (count == 5) return true; // If count reaches 5, return true
                }
            }
        }
        return false; // If no number reaches 5, return false
    }

    // Checks if all six dice are the same
    public boolean isSixOfAKind() {
        for (int i = 1; i < diceRolls.length; i++) {
            if (diceRolls[i] != diceRolls[0]) return false;
        }
        return true;
    }

    // Checks if the dice form a straight from 1 to 6
    public boolean isStraightOneToSix() {
        int[] sortedDice = diceRolls.clone();
        Arrays.sort(sortedDice);
        for (int i = 0; i < sortedDice.length; i++) {
            if (sortedDice[i] != i + 1) return false;
        }
        return true;
    }

    // Checks for two pairs in the dice
    private boolean checkTwoPairs(int[] dice) {
        int pairs = 0;
        for (int i = 0; i < dice.length - 1; i++) {
            if (dice[i] == dice[i + 1]) {
                pairs++;
                i++; 
            }
        }
        return pairs >= 2;
    }

    // Checks for three of a kind in the dice
    private boolean checkThreeOfAKind(int[] dice) {
        for (int i = 0; i <= dice.length - 3; i++) {
            if (dice[i] == dice[i + 2]) return true;
        }
        return false;
    }

    // Checks for a little straight (four unique consecutive numbers)
    private boolean checkLittleStraight(int[] dice) {
        ArrayList<Integer> uniqueDice = new ArrayList<>();
        for (int die : dice) {
            if (!uniqueDice.contains(die)) {
                uniqueDice.add(die);
            }
        }
        for (int i = 0; i <= uniqueDice.size() - 4; i++) {
            if (uniqueDice.get(i + 3) - uniqueDice.get(i) == 3 &&
                uniqueDice.get(i + 1) == uniqueDice.get(i) + 1 &&
                uniqueDice.get(i + 2) == uniqueDice.get(i) + 2) {
                return true;
            }
        }
        return false;
    }

    // Checks for a full house (three of a kind and a pair)
    private boolean checkFullHouse(int[] dice) {
        return checkThreeOfAKind(dice) && checkTwoPairs(dice);
    }

    // Checks for four of a kind in the dice
    private boolean checkFourOfAKind(int[] dice) {
        for (int i = 0; i <= dice.length - 4; i++) {
            if (dice[i] == dice[i + 3]) return true;
        }
        return false;
    }

    // Checks for a large straight (five unique consecutive numbers)
    private boolean checkLargeStraight(int[] dice) {
        ArrayList<Integer> uniqueDice = new ArrayList<>();
        for (int die : dice) {
            if (!uniqueDice.contains(die)) {
                uniqueDice.add(die);
            }
        }
        return uniqueDice.equals(Arrays.asList(1, 2, 3, 4, 5)) || 
               uniqueDice.equals(Arrays.asList(2, 3, 4, 5, 6));
    }

    // Checks if all dice are even
    private boolean checkAllEven(int[] dice) {
        for (int die : dice) {
            if (die % 2 != 0) return false;
        }
        return true;
    }

    // Checks if all dice are odd
    private boolean checkAllOdd(int[] dice) {
        for (int die : dice) {
            if (die % 2 == 0) return false;
        }
        return true;
    }

    // Checks if the sum of the dice is less than or equal to a threshold
    private boolean checkSumLessThanOrEqual(int[] dice, int threshold) {
        int sum = 0;
        for (int die : dice) sum += die;
        return sum <= threshold;
    }

    // Checks if the sum of the dice is greater than or equal to a threshold
    private boolean checkSumGreaterThanOrEqual(int[] dice, int threshold) {
        int sum = 0;
        for (int die : dice) sum += die;
        return sum >= threshold;
    }

    // Checks for three pairs in the dice
    private boolean checkThreePairs(int[] dice) {
        int pairs = 0;
        for (int i = 0; i < dice.length - 1; i++) {
            if (dice[i] == dice[i + 1]) {
                pairs++;
                i++;
            }
        }
        return pairs == 3;
    }

    // Checks for two three-of-a-kind in the dice
    private boolean checkTwoThreeOfAKind(int[] dice) {
        int firstThree = -1;
        for (int i = 0; i <= dice.length - 3; i++) {
            if (dice[i] == dice[i + 2]) {
                firstThree = dice[i];
                break;
            }
        }
        if (firstThree == -1) return false;
        for (int i = 0; i <= dice.length - 3; i++) {
            if (dice[i] != firstThree && dice[i] == dice[i + 2]) return true;
        }
        return false;
    }

    // Checks for four of a kind and a pair in the dice
    private boolean checkFourOfAKindAndPair(int[] dice) {
        return checkFourOfAKind(dice) && checkTwoPairs(dice);
    }
}
