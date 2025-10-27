
package Scrabble;
/**
 * A scrabble game object to be used within a GUI or testing files. This game
 * has checks for valid moves, total score, and generation of each of the parts
 * of the game.
 * @author BeckDun
 */
import java.io.*;
import java.util.*;

public class ScrabbleGame {
    private ArrayList<Letter> playerHand;
    private ArrayList<Letter> computerHand;
    private Trie dictionary;
    private Tile[][] board;
    private int size;
    private ArrayList<Letter> bag;
    private HashMap<Character,Integer> bagMap;
    private boolean gui;

    /**
     * Creates a new ScrabbleGame with GUI mode disabled.
     */
    public ScrabbleGame(){
        gui = false;
    }

    /**
     * Creates a new ScrabbleGame.
     * @param gui If true, initializes the game for use with a graphical user interface
     */
    public ScrabbleGame(boolean gui) {
        this.gui = gui;
    }

    /**
     * Gets the current board state.
     * @return The 2D array of Tile objects representing the board
     */
    public Tile[][] getBoard() {
        return board;
    }

    /**
     * Getter method for the bag of letters
     * @return bag of letters
     */
    public ArrayList<Letter> getBag() {
        return bag;
    }

    /**
     * Gets the total score from a move. Score from both horizontal and vertical
     * is taken into account
     * @param coordinates coodinates of the move that was just played
     * @return int of the total score from a given move
     */
    public int getTotalScore(LinkedList<Position> coordinates) {
        int score = 0;

        if (coordinates.size() == 7) {
            score += 50;
        }

        char dir;
        if (coordinates.size() < 2) {
            dir = determineDirection(coordinates.getFirst());
        } else if (coordinates.getFirst().row == coordinates.get(1).row) {
            dir = 'H';
        } else {
            dir = 'V';
        }


        if (dir == 'H') {
            int[] wordCoordinates = findWordStart(coordinates.getFirst(), dir);
            score += scoreWord(wordCoordinates, 'H');

            for (Position coordinate : coordinates) {
                int[] wordStart = findWordStart(coordinate, 'V');
                String crossWord = getWord(wordStart, 'V');
                if (crossWord != null && crossWord.length() > 1) {
                    score += scoreWord(wordStart, 'V');
                }
            }
        } else {
            int[] wordCoordinates = findWordStart(coordinates.getFirst(), dir);
            score += scoreWord(wordCoordinates, 'V');

            for (Position coordinate : coordinates) {
                int[] wordStart = findWordStart(coordinate, 'H');
                String crossWord = getWord(wordStart, 'H');
                if (crossWord != null && crossWord.length() > 1) {
                    score += scoreWord(wordStart, 'H');
                }
            }
        }

        return score;
    }

    /**
     * Resets the multipliers for the tiles at the given positions
     * Call this after scoring a move
     * @param coordinates The positions where multipliers should be reset
     */
    private void resetMultipliers(LinkedList<Position> coordinates) {
        for (Position coordinate : coordinates) {
            int curX = coordinate.row;
            int curY = coordinate.col;
            board[curX][curY].setLetterMult(1);
            board[curX][curY].setWordMult(1);
        }
    }

    /**
     * Scores a word starting at a given location and proceeding in the
     * specified direction. Takes into account letter and word multipliers.
     * @param location The [row, col] coordinates of the start of the word
     * @param dir The direction of the word ('H' for horizontal, 'V' for vertical)
     * @return The score for the word
     */
    private int scoreWord(int[] location, char dir) {
        int curRow = location[0];
        int curCol = location[1];
        int score = 0;
        int wordMult = 1;

        if(dir == 'V') {
            while( curRow < size && !board[curRow][curCol].isEmpty()) {
                Letter curLetter = board[curRow][curCol].getLetter();
                Tile curTile= board[curRow][curCol];
                score += curLetter.getPoints() * curTile.getLetterMult();
                wordMult *= curTile.getWordMult();
                curRow++;

            }
        } else if(dir == 'H') {
            while( curCol < size && !board[curRow][curCol].isEmpty()) {
                Letter curLetter = board[curRow][curCol].getLetter();
                Tile curTile= board[curRow][curCol];
                score += curLetter.getPoints() * curTile.getLetterMult();
                wordMult *= curTile.getWordMult();
                curCol++;
            }

        }

        return score * wordMult;
    }

    /**
     * Finds the starting position of a word containing the given coordinate
     * and proceeding in the specified direction.
     * @param coord The position that is part of the word
     * @param dir The direction of the word ('H' for horizontal, 'V' for vertical)
     * @return An array containing the [row, col] coordinates of the start of the word
     */
    public int[] findWordStart(Position coord, char dir) {
        int curRow = coord.row;
        int curCol = coord.col;

        if(dir == 'V') {
            while(((curRow - 1) >= 0) &&
                    (board[curRow-1][curCol].getLetter().getChar() != '\0')) {
                curRow --;
            }
        } else if(dir == 'H') {
            while(((curCol - 1) >= 0) &&
                    (board[curRow][curCol-1].getLetter().getChar() != '\0')) {
                curCol --;
            }
        }

        return new int[]{curRow,curCol};
    }

    /**
     * Evaluates the score of a move without modifying the actual game state
     * This is important for computer player move evaluation
     * @param positions The positions where tiles are placed
     * @param newLetters The map of positions to letters
     * @return The score for the move
     */
    public int evaluateScore(LinkedList<Position> positions, HashMap<Position, Letter> newLetters) {
        if (positions.isEmpty()) {
            return -1;
        }

        Tile[][] tempBoard = createBoardCopy();

        for (Map.Entry<Position, Letter> entry : newLetters.entrySet()) {
            Position pos = entry.getKey();
            tempBoard[pos.row][pos.col].setLetter(entry.getValue());
        }

        int score = 0;

        // Bingo bonus
        if (positions.size() == 7) {
            score += 50;
        }

        char dir;
        if (positions.size() < 2) {
            dir = determineDirection(tempBoard, positions.getFirst());
        } else if (positions.getFirst().row == positions.get(1).row) {
            dir = 'H';
        } else {
            dir = 'V';
        }

        if (dir == 'H') {
            Position start = findWordStartOnBoard(tempBoard, positions.getFirst(), dir);
            String mainWord = getWordFromBoard(tempBoard, start, 'H');
            if (mainWord != null && mainWord.length() >= 2) {
                score += scoreWordOnBoard(tempBoard, start, 'H');
            }

            for (Position coordinate : positions) {
                Position crossStart = findWordStartOnBoard(tempBoard, coordinate, 'V');
                String crossWord = getWordFromBoard(tempBoard, crossStart, 'V');
                if (crossWord != null && crossWord.length() > 1) {
                    score += scoreWordOnBoard(tempBoard, crossStart, 'V');
                }
            }
        } else {
            Position start = findWordStartOnBoard(tempBoard, positions.getFirst(), dir);
            String mainWord = getWordFromBoard(tempBoard, start, 'V');
            if (mainWord != null && mainWord.length() >= 2) {
                score += scoreWordOnBoard(tempBoard, start, 'V');
            }

            for (Position coordinate : positions) {
                Position crossStart = findWordStartOnBoard(tempBoard, coordinate, 'H');
                String crossWord = getWordFromBoard(tempBoard, crossStart, 'H');
                if (crossWord != null && crossWord.length() > 1) {
                    score += scoreWordOnBoard(tempBoard, crossStart, 'H');
                }
            }
        }

        return score;
    }

    /**
     * Creates a deep copy of the current board
     * @return A new copy of the board
     */
    private Tile[][] createBoardCopy() {
        Tile[][] copy = new Tile[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Tile original = board[i][j];
                if (original.isEmpty()) {
                    copy[i][j] = new Tile(original.getWordMult(), original.getLetterMult());
                } else {
                    Letter letterCopy = new Letter(original.getLetter().getChar(), original.getLetter().getPoints());
                    copy[i][j] = new Tile(letterCopy);
                }
            }
        }

        return copy;
    }

    /**
     * Determines the direction of a move on a given board
     * @param board The board to check
     * @param pos The position to check
     * @return 'H' for horizontal or 'V' for vertical
     */
    private char determineDirection(Tile[][] board, Position pos) {
        int row = pos.row;
        int col = pos.col;

        boolean horizontalAdjacent =
                (col > 0 && !board[row][col-1].isEmpty()) ||
                        (col < size-1 && !board[row][col+1].isEmpty());

        return horizontalAdjacent ? 'H' : 'V';
    }

    /**
     * Finds the start position of a word on a given board
     * @param board The board to check
     * @param pos The position within the word
     * @param dir The direction (H or V)
     * @return The position of the start of the word
     */
    private Position findWordStartOnBoard(Tile[][] board, Position pos, char dir) {
        int row = pos.row;
        int col = pos.col;

        if (dir == 'H') {
            while (col > 0 && !board[row][col-1].isEmpty()) {
                col--;
            }
        } else {
            while (row > 0 && !board[row-1][col].isEmpty()) {
                row--;
            }
        }

        return new Position(row, col);
    }

    /**
     * Gets a word from a board starting at a position in a direction
     * @param board The board to check
     * @param start The starting position
     * @param dir The direction (H or V)
     * @return The word as a string
     */
    private String getWordFromBoard(Tile[][] board, Position start, char dir) {
        StringBuilder word = new StringBuilder();
        int row = start.row;
        int col = start.col;

        if (dir == 'H') {
            while (col < size && !board[row][col].isEmpty()) {
                word.append(board[row][col].getLetter().getChar());
                col++;
            }
        } else {
            while (row < size && !board[row][col].isEmpty()) {
                word.append(board[row][col].getLetter().getChar());
                row++;
            }
        }

        return word.length() > 1 ? word.toString() : null;
    }

    /**
     * Scores a word on a given board
     * @param board The board to use
     * @param start The starting position of the word
     * @param dir The direction of the word (H or V)
     * @return The score of the word
     */
    private int scoreWordOnBoard(Tile[][] board, Position start, char dir) {
        int row = start.row;
        int col = start.col;
        int score = 0;
        int wordMult = 1;

        if (dir == 'H') {
            while (col < size && !board[row][col].isEmpty()) {
                Letter letter = board[row][col].getLetter();
                Tile tile = board[row][col];
                score += letter.getPoints() * tile.getLetterMult();
                wordMult *= tile.getWordMult();
                col++;
            }
        } else {
            while (row < size && !board[row][col].isEmpty()) {
                Letter letter = board[row][col].getLetter();
                Tile tile = board[row][col];
                score += letter.getPoints() * tile.getLetterMult();
                wordMult *= tile.getWordMult();
                row++;
            }
        }

        return score * wordMult;
    }

    /**
     * Applies a move to the board and returns the score
     * This method should be used to make the final move after evaluation
     * @param newLetters The letters to place
     * @return The score for the move
     */
    public int makeMove(HashMap<Position, Letter> newLetters) {
        LinkedList<Position> positions = new LinkedList<>(newLetters.keySet());

        if (positions.isEmpty()) {
            return -1;
        }

        for (Map.Entry<Position, Letter> entry : newLetters.entrySet()) {
            Position pos = entry.getKey();
            board[pos.row][pos.col].setLetter(entry.getValue());
        }

        int score = getTotalScore(positions);
        resetMultipliers(positions);

        return score;
    }

    /**
     * Checks to see if a move is valid for the given board
     * @param locations locations of letters to be placed on the board
     * @return true if the move was valid
     */
    public boolean validMove(HashMap<Position,Letter> locations) {
        if (locations.isEmpty()) {
            return false;
        }

        ArrayList<Position> coordinates = new ArrayList<>(locations.keySet());
        ArrayList<Position> filledPositions = getFilledTiles();

        for (Map.Entry<Position, Letter> entry : locations.entrySet()) {
            Position position = entry.getKey();
            Letter letter = entry.getValue();
            board[position.row][position.col].setLetter(letter);
        }

        boolean isFirstMove = filledPositions.isEmpty();

        if (isFirstMove) {
            Position middle = new Position((size-1)/2,(size-1)/2);
            boolean touchesMiddle = false;
            for(Position position: coordinates) {
                if(position.row == middle.row && position.col == middle.col) {
                    touchesMiddle = true;
                }
            }

            if(!touchesMiddle) {
                return false;
            }
        }

        if (isFirstMove && coordinates.size() < 2) {
            removeLetters(locations);
            return false;
        }

        for (Position newPos : coordinates) {
            for (Position filledPos : filledPositions) {
                if (newPos.equals(filledPos)) {
                    removeLetters(locations);
                    return false;
                }
            }
        }


        if (coordinates.size() > 1) {
            Position first = coordinates.get(0);
            boolean sameRow = true;
            boolean sameCol = true;

            for (Position pos : coordinates) {
                if (pos.row != first.row) sameRow = false;
                if (pos.col != first.col) sameCol = false;
            }

            if (!sameRow && !sameCol) {
                removeLetters(locations);
                return false;
            }
        }

        if (!isFirstMove) {
            boolean adjacent = false;
            for (Position newPos : coordinates) {
                int row = newPos.row;
                int col = newPos.col;

                //TODO make a function
                if ((row > 0 && !board[row-1][col].isEmpty() &&
                        !containsPosition(coordinates, row-1, col)) ||
                        (row < size-1 && !board[row+1][col].isEmpty() &&
                                !containsPosition(coordinates, row+1, col)) ||
                        (col > 0 && !board[row][col-1].isEmpty() &&
                                !containsPosition(coordinates, row, col-1)) ||
                        (col < size-1 && !board[row][col+1].isEmpty() &&
                                !containsPosition(coordinates, row, col+1))) {
                    adjacent = true;
                    break;
                }
            }
            if (!adjacent) {
                removeLetters(locations);
                return false;
            }
        }

        char direction = coordinates.size() > 1 ?
                (coordinates.get(0).row == coordinates.get(1).row ? 'H' : 'V') :
                (isFirstMove ? 'H' : determineDirection(coordinates.get(0)));

        if (direction == 'H') {
            coordinates.sort(Comparator.comparingInt(a -> a.col));
        } else {
            coordinates.sort(Comparator.comparingInt(a -> a.row));
        }

        boolean hasGaps = false;
        if (coordinates.size() > 1) {
            if (direction == 'H') {
                int row = coordinates.get(0).row;
                for (int i = 0; i < coordinates.size() - 1; i++) {
                    int col1 = coordinates.get(i).col;
                    int col2 = coordinates.get(i+1).col;

                    for (int col = col1 + 1; col < col2; col++) {
                        if (board[row][col].isEmpty()) {
                            hasGaps = true;
                            break;
                        }
                    }
                }
            } else {
                int col = coordinates.get(0).col;
                for (int i = 0; i < coordinates.size() - 1; i++) {
                    int row1 = coordinates.get(i).row;
                    int row2 = coordinates.get(i+1).row;

                    for (int row = row1 + 1; row < row2; row++) {
                        if (board[row][col].isEmpty()) {
                            hasGaps = true;
                            break;
                        }
                    }
                }
            }
        }

        if (hasGaps) {
            removeLetters(locations);
            return false;
        }

        ArrayList<String> words = new ArrayList<>();
        int[] start = findWordStart(coordinates.get(0), direction);
        String mainWord = getWord(start, direction);
        if (mainWord != null) {
            words.add(mainWord);
        }

        // select dir
        char crossDirection = direction == 'H' ? 'V' : 'H';

        for (Position pos : coordinates) {
            int[] crossStart = findWordStart(pos, crossDirection);
            String crossWord = getWord(crossStart, crossDirection);
            if (crossWord != null && crossWord.length() > 1) {
                words.add(crossWord);
            }
        }


        for (String word : words) {
            if (!dictionary.searchForWord(word)) {
                removeLetters(locations);
                return false;
            }
        }

        return true;

    }

    /**
     * Removes letters from the board at the specified positions.
     * @param locations The map of positions to letters to remove
     */
    private void removeLetters(HashMap<Position, Letter> locations) {
        for (Position position : locations.keySet()) {
            board[position.row][position.col].removeLetter();
        }
    }

    /**
     * Checks if a list of positions contains a specific row and column.
     * @param coordinates The list of positions to check
     * @param row The row to look for
     * @param col The column to look for
     * @return true if the position is in the list, false otherwise
     */
    private boolean containsPosition(ArrayList<Position> coordinates,
                                     int row, int col) {
        for (Position pos : coordinates) {
            if (onBoard(row,col) && onBoard(pos.row,pos.col) &&
                    pos.row == row && pos.col == col) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a position is within the bounds of the board.
     * @param row The row coordinate to check
     * @param col The column coordinate to check
     * @return true if the position is on the board, false otherwise
     */
    public boolean onBoard(int row, int col) {
        if(row >=0 && row < size && col >= 0 && col < size) {
            return true;
        }
        return false;
    }

    /**
     * Determines the direction of a word being played based on adjacent filled
     * tiles.
     * @param position The position to check
     * @return 'H' for horizontal, 'V' for vertical
     */
    private char determineDirection(Position position) {
        int row = position.row;
        int col = position.col;

        boolean horizontalAdjacent =
                (col > 0 && !board[row][col-1].isEmpty()) ||
                        (col < size-1 && !board[row][col+1].isEmpty());

        boolean verticalAdjacent =
                (row > 0 && !board[row-1][col].isEmpty()) ||
                        (row < size-1 && !board[row+1][col].isEmpty());

        return horizontalAdjacent ? 'H' : 'V';
    }

    /**
     * Gets the word starting at a given position and proceeding in the
     * specified direction.
     * @param start The [row, col] coordinates of the start of the word
     * @param dir The direction of the word ('H' for horizontal, 'V' for vertical)
     * @return The word as a string, or null if no valid word is found
     */
    public String getWord(int[] start, char dir) {
        int curRow = start[0];
        int curCol = start[1];
        StringBuilder wordBuilder = new StringBuilder();

        // Input validation
        if (curRow < 0 || curRow >= size || curCol < 0 || curCol >= size) {
            return null;
        }

        if (dir == 'V') {
            while (curRow < size && !board[curRow][curCol].isEmpty()) {
                String letter = board[curRow][curCol].getLetter().toString();
                wordBuilder.append(letter);
                curRow++;
            }
        } else if (dir == 'H') {
            while (curCol < size && !board[curRow][curCol].isEmpty()) {
                String letter = board[curRow][curCol].getLetter().toString();
                wordBuilder.append(letter);
                curCol++;
            }
        }

        if (wordBuilder.length() > 1) {
            return wordBuilder.toString().toLowerCase();
        }

        return null;
    }

    /**
     * Finds all the filled tiles on the board
     * @return list of all the filled tiles
     */
    public ArrayList<Position> getFilledTiles() {
        ArrayList<Position> locations = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(!board[i][j].isEmpty()) {
                    locations.add(new Position(i,j));
                }
            }
        }
        return locations;
    }


    /**
     * generates the bag of letters to be used
     */
    public void generateBag() {
        bag = new ArrayList<>();
        bagMap = new HashMap<>();

        Scanner s;
        InputStream in;
        in = getClass().getClassLoader().
                getResourceAsStream("scrabble_tiles.txt");

        s = new Scanner(in);

        int points = 0;
        char currLetter = '\0';
        int frequency = 0;

        while(s.hasNextLine()) {
            String[] line = s.nextLine().split(" ");

            currLetter = line[0].charAt(0);
            points = Integer.parseInt(line[1]);
            frequency = Integer.parseInt(line[2]);

            for (int i = 0; i < frequency; i++) {
                bag.add(new Letter(currLetter, points));
                bagMap.put(currLetter,points);
            }
        }
    }


    /**
     * Prints the bag of letters for a game of scrabble
     */
    public void printBag() {
        for (Letter l : bag) {
            System.out.println(l + " " + l.getPoints());
        }

    }


    /**
     * Loads a dictionary into a Trie data structure
     * @param dictionary File to be read and saved into the Trie
     */
    public void loadDictionary(File dictionary) {
        this.dictionary = new Trie();

        try(BufferedReader br=new BufferedReader(new FileReader(dictionary))) {
            String word;
            while ((word = br.readLine()) != null) {
                this.dictionary.insert(word);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads a dictionary into a Trie data structure
     * @param s Scanner to be read and saved into the Trie
     */
    public void loadDictionary(Scanner s) {
        this.dictionary = new Trie();
            while (s.hasNextLine()) {
                this.dictionary.insert(s.nextLine());
            }
    }

    /**
     * Loads the board passed by the scanner. With letters and blanks.
     * @param scanner input for the board to be read from
     */
    public void loadBoard(Scanner scanner) {
        size = scanner.nextInt();
        scanner.nextLine();
        board = new Tile[size][size];
        int wordMult = 1;
        int letterMult = 1;



        for (int i = 0; i < size; i++) {
            String line = scanner.nextLine().trim();
            String[] row = line.split("\\s+");
            for (int j = 0; j < size; j++) {
                String tileString = row[j];

                if(tileString.contains(".")) {
                    char[] chars = tileString.toCharArray();
                    if(chars.length > 0 && chars[0] != '.') {
                        wordMult = Character.getNumericValue(chars[0]);
                    }
                    if(chars.length > 1 && chars[1] != '.') {
                        letterMult = Character.getNumericValue(chars[1]);
                    }
                    board[i][j] = new Tile(wordMult, letterMult);
                    wordMult = letterMult = 1;
                } else {
                    Letter temp = new Letter(tileString.charAt(0), 0);
                    board[i][j] = new Tile(createLetter(temp));
                }
            }
        }
    }

    /**
     * Creates a letter object with the appropriate points value.
     * For GUI mode, removes the letter from the bag.
     *
     * @param l The letter template to create
     * @return A new Letter object with the appropriate points
     */
    private Letter createLetter(Letter l) {
        if(!gui) {
            Character character = l.getChar();
            if(character >= 'A' && character <= 'Z') {
                Letter letter = new Letter('*',0);
                letter.setBlank(character);
                return letter;
            }
            return new Letter(character, bagMap.get(character));
        } else {
            for (int i = 0; i < bag.size(); i++) {
                Letter bagLetter = bag.get(i);
                if (bagLetter.getChar() == l.getChar()) {
                    return bag.remove(i);
                }
            }
            return new Letter(l.getChar(), l.getChar() == '*' ? 0 :
                    bagMap.getOrDefault(l.getChar(), 1));
        }

    }

    /**
     * Prints the board of the current game
     */
    public void printBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(j == size-1) {
                    System.out.print(board[i][j]);
                } else {
                    System.out.print(board[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Checks if a tile at a given position is empty.
     * @param p The position to check
     * @return true if the tile is empty, false otherwise
     */
    public boolean tileIsEmpty(Position p) {
        if(onBoard(p.row,p.col) && board[p.row][p.col].isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Checks if a tile at a given position is filled (has a letter).
     * @param p The position to check
     * @return true if the tile is filled, false otherwise
     */
    public boolean tileIsFilled(Position p) {
        if(onBoard(p.row,p.col) && !board[p.row][p.col].isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * getter method for the size of the board
     * @return size of the given board
     */
    public int getSize() {
        return this.size;
    }

    /**
     * getter method for the dictionary. used by the players
     * @return dictionary for scrabble
     */
    public Trie getDictionary() {
        return dictionary;
    }
}