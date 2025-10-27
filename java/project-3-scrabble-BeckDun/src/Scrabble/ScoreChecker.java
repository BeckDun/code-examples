package Scrabble;

import java.io.*;
import java.util.*;

/**
 * A program that reads in two different boards and
 * finds the difference between them. Then is checks if the move was valid and
 * if so, scores the move.
 * This program is used to help understand if the scoring system in ScrabbleGame
 * is working properly
 * @author BeckDun
 */
public class ScoreChecker {
    private static boolean invalidBoard;
    private static boolean invalidTiles;

    public static void main(String[] args) {
        invalidBoard = false;
        invalidTiles = false;
        Scanner s = new Scanner(System.in);
        ScrabbleGame scrabbleGame1 = new ScrabbleGame();
        ScrabbleGame scrabbleGame2 = new ScrabbleGame();

        scrabbleGame1.loadDictionary(new File(args[0]));

        while(s.hasNext()) {
            scrabbleGame1.generateBag();
            scrabbleGame2.generateBag();
            scrabbleGame1.loadBoard(s);
            scrabbleGame2.loadBoard(s);
            System.out.println("original board:");
            scrabbleGame1.printBoard();
            System.out.println("result board:");
            scrabbleGame2.printBoard();

            HashMap<Position, Letter> difference = getboardDiff(scrabbleGame1,scrabbleGame2);
            LinkedList<Position> coordsPlayed = new LinkedList<>(difference.keySet());


            if(invalidTiles) {
                System.out.println();
                invalidTiles = false;
                invalidBoard = false;
                continue;
            }

            if(difference.isEmpty()) {
                System.out.println("play is empty");
                System.out.println("play is not legal\n");
                continue;
            }



            if(invalidBoard) {
                if(coordsPlayed.size() > 0) {
                    printPlay(coordsPlayed, difference);
                }
                System.out.println("play is not legal\n");
                continue;
            }

            char direction;
            if (coordsPlayed.size() >= 2) {
                if(coordsPlayed.get(0).row == coordsPlayed.get(1).row) {
                    direction = 'H';
                    coordsPlayed.sort(Comparator.comparingInt(a -> a.col));
                } else {
                    direction = 'V';
                    coordsPlayed.sort(Comparator.comparingInt(a -> a.row));
                }
            }

            printPlay(coordsPlayed,difference);


            if(scrabbleGame1.validMove(difference)) {
                System.out.println("play is legal");
                System.out.println("score is " + scrabbleGame1.getTotalScore(coordsPlayed) + "\n");
            } else {
                System.out.println("play is not legal\n");
            }

        }




    }

    private static void printPlay(LinkedList<Position> coordsPlayed, HashMap<Position,Letter> difference) {
        System.out.print("play is ");
        for (int i = 0; i < coordsPlayed.size(); i++) {
            Position pos = coordsPlayed.get(i);
            System.out.print(difference.get(pos).toString()
                    + " at (" + pos.row + ", " + pos.col + ")");

            if (i < coordsPlayed.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }

    private static HashMap<Position, Letter> getboardDiff(ScrabbleGame game1, ScrabbleGame game2) {
        if(game1.getSize() != game2.getSize()) {
            invalidBoard = true;
            return new HashMap<>();
        }

        HashMap<Position,Letter> diff = new HashMap<>();
        Tile[][] board1 = game1.getBoard();
        Tile[][] board2 = game2.getBoard();

        for (int i = 0; i < game1.getSize(); i++) {
            for (int j = 0; j < game1.getSize(); j++) {
                if(!board1[i][j].equals(board2[i][j])) {
                    if (!board1[i][j].isEmpty() && board2[i][j].isEmpty()) {
                        System.out.println("Incompatible boards: tile removed at (" + i + ", " + j + ")");
                        invalidTiles = true;
                        invalidBoard = true;
                        return new HashMap<>();
                    } else if (board1[i][j].isEmpty() && board2[i][j].isEmpty() &&
                            (board1[i][j].getWordMult() != board2[i][j].getWordMult() ||
                                    board1[i][j].getLetterMult() != board2[i][j].getLetterMult())) {
                        System.out.println("Incompatible boards: multiplier mismatch at (" + i + ", " + j + ")");
                        invalidTiles = true;
                        invalidBoard = true;
                        return new HashMap<>();
                    } else if(board1[i][j].isEmpty() && !board2[i][j].isEmpty()) {
                        diff.put(new Position(i, j), board2[i][j].getLetter());
                    }
                }
            }
        }
        return diff;
    }
}
