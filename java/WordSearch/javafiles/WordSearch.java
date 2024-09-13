package javafiles;
/**
 * @author Beckett Dunlavy (github.com/BeckDun)
 * Word seach solver. Reads in 3 command line arguments.
 * First arg is the file of words to search for
 * Second arg is the puzzle file
 * Third arg is the direction to search for.
 * Uses Direction enum N,S,E,W,NE,SE,SW,NW
 * Uses a Trie data structure to organize the list of words.
 * Also uses a WordsFound data type to print output of all the words found,
 * where they were found and what direction.
 */
import java.nio.file.*;
import java.util.*;
import java.io.*;

public class WordSearch {
    public static void main(String[] args) {
        Scanner wordsReader = null;
        Scanner puzzleReader = null;
        String arg2 = null;

        try {
            Path wordsFile = Paths.get(args[0]);
            Path puzzleFile = Paths.get(args[1]);
            wordsReader = new Scanner(wordsFile);
            puzzleReader = new Scanner(puzzleFile);
            arg2 = args[2];
        } catch (IOException x) {
            System.out.println(x);
        }

        // read in directions
        Direction[] directions;
        switch (arg2) {
            case "ALL": directions = Direction.ALL;
            break;
            case "DIAGONAL": directions = Direction.DIAGONAL;
            break;
            case "HORIZVERT": directions = Direction.HORIZVERT;
            break;
            case "FORWARD": directions = Direction.FORWARD;
            break;
            default: directions = Direction.ALL;
        }

        // read in char array
        int maxRow = puzzleReader.nextInt();
        int maxCol = puzzleReader.nextInt();
        puzzleReader.nextLine();
        char[][] puzzle = new char[maxRow][maxCol];

        for (int i = 0; i < maxRow; i++) {
            String line = puzzleReader.nextLine();
            char[] lineSplit = line.toCharArray();
            for (int j = 0; j < lineSplit.length; j++) {
                puzzle[i][j] = lineSplit[j];
            }
        }

        // read in words to trei
        Trie wordList = new Trie();
        while (wordsReader.hasNextLine()) {
            wordList.insert(wordsReader.nextLine());
        }

        ArrayList<WordsFound> words =
                WordSearch(maxRow, maxCol, puzzle, wordList, directions);

        Collections.sort(words, new Comparator<WordsFound>() {
            @Override
            public int compare(WordsFound o1, WordsFound o2) {
                if(o1.getWord().equals(o2.getWord())) {
                    if(o1.getRow() == o2.getRow()) {
                        if(o1.getCol() == o2.getCol()) {
                            return o1.getDirection().compareTo
                                    (o2.getDirection());
                        }
                        return o1.getCol()-o2.getCol();
                    }
                    return o1.getRow() - o2.getRow();
                }
                return o1.getWord().compareTo(o2.getWord());
            }
        });

        for(WordsFound s: words) {
            System.out.println(s.toString());
        }

    }

    /**
     * Searches for words in the puzzle given by command line.
     * Returns array list full of WordsFound variables to be printed out.
     * @param maxRow max rows for the puzzle as an int
     * @param maxCol max columns for the puzzle as an int
     * @param puzzle takes a 2D array of the puzzle
     * @param wordlist takes the wordlist in a Trie data structure
     * @param directions the directions used for the word search as Enum array
     * @return returns an array list of WordsFound.
     */
    public static ArrayList<WordsFound> WordSearch
            (int maxRow, int maxCol,
             char[][] puzzle, Trie wordlist, Direction[] directions) {
        ArrayList<WordsFound> wordsFoundList = new ArrayList<>();

        for (int i = 0; i < maxRow; i++) {
            for (int j = 0; j < maxCol; j++) {
                for (int k = 0; k < directions.length; k++) {
                    Direction currDir = directions[k];
                    int colOff = currDir.colOffset;
                    int rowOff = currDir.rowOffset;
                    int colIncrement = colOff;
                    int rowIncrement = rowOff;
                    String word = String.valueOf(puzzle[i][j]);
                    ArrayList<Character> characters = new ArrayList<>();
                    characters.add(puzzle[i][j]);

                    if (wordlist.startsWith(word)) {
                        while (onBoard(rowOff+i,
                                colOff+j,maxRow,maxCol)) {
                            word = word + (puzzle[i + rowOff][j + colOff]);
                            characters.add(puzzle[i + rowOff][j + colOff]);
                            if (wordlist.searchPrefix(word)) {
                                if (wordlist.searchForWord(word)) {
                                    WordsFound wordFound =
                                        new WordsFound(word, i, j, currDir);
                                    wordsFoundList.add(wordFound);
                                }
                                rowOff += rowIncrement;
                                colOff += colIncrement;
                            } else {
                                break;
                            }
                        }
                        Iterator<Character> charIter = characters.iterator();
                    }
                }
            }
        }
        return wordsFoundList;
    }

    private static boolean onBoard(int row, int col, int maxRow,int maxCol) {
        if (row >= 0 && col >=0 && maxRow > row && maxCol > col) {
            return true;
        }
        return false;
    }
}
