
package Scrabble;

import java.io.File;
import java.util.*;
/**
 * This program validates the computer player for the scrabble game.
 * It reads in a board and a tray of letters. Then finds the play that will
 * give the highest amount of points. If no play is found it will not play and
 * signal in the output that no moves are availible.
 * @author BeckDun
 */
public class ScrabbleSolver {
    public static void main(String[] args) {


        ScrabbleGame game = new ScrabbleGame();
        game.loadDictionary(new File(args[0]));

        Scanner scanner = new Scanner(System.in);


        while(scanner.hasNext()) {
            game.generateBag();
            game.loadBoard(scanner);

            if (!scanner.hasNextLine()) {
                break;
            }
            String trayString = scanner.nextLine().trim();
            ArrayList<Letter> computerHand = convertTrayToHand(trayString, game);

            System.out.println("Input Board:");
            game.printBoard();
            System.out.println("Tray: " + trayString);

            Computer computerPlayer = new Computer(game, computerHand);
            computerPlayer.findAllWords();
            HashMap<Position, Letter> bestMove = computerPlayer.getBestMove();

            if (bestMove.isEmpty()) {
                System.out.println("No valid move found.");
                System.out.println();
                continue;
            }

            LinkedList<Position> positions = new LinkedList<>(bestMove.keySet());
            if (positions.isEmpty()) {
                System.out.println("No valid move found.");
                System.out.println();
                continue;
            }
            game.makeMove(computerPlayer.getBestMove());
            int score = computerPlayer.getBestScore();
            for (Position pos : positions) {
                game.getBoard()[pos.row][pos.col].setLetterMult(1);
                game.getBoard()[pos.row][pos.col].setWordMult(1);
            }

            System.out.println("Solution " +
                    computerPlayer.getBestWord() + " has " + score + " points");
            System.out.println("Solution Board:");
            game.printBoard();
            System.out.println();
        }

        scanner.close();
    }

    /**
     * Converts a string representation of a letter tray to an ArrayList of
     * Letter objects.
     * @param trayString The string representation of the tray
     * @param game The current game state (used to get letter point values)
     * @return An ArrayList of Letter objects representing the tray
     */
    private static ArrayList<Letter> convertTrayToHand(String trayString, ScrabbleGame game) {
        ArrayList<Letter> hand = new ArrayList<>();
        ArrayList<Letter> bag = game.getBag();

        for(Character c : trayString.toCharArray()) {
            if(c == '*') {
                hand.add(new Letter('*', 0));
            } else {
                c = Character.toLowerCase(c);
                Letter temp = new Letter(c, 0);
                int index = bag.indexOf(temp);
                if(index == -1) {
                    hand.add(temp);
                } else {
                    Letter letter = bag.get(index);
                    hand.add(new Letter(letter.getChar(), letter.getPoints()));
                }
            }
        }
        return hand;
    }
}