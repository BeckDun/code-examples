package Scrabble;

import java.util.*;

/**
 * This is a scrabble computer player that finds the highest scoring move
 * with a given tray and board.
 * @author BeckDun
 */
public class Computer {
    private ScrabbleGame game;
    private Trie dictionary;
    private int boardSize;
    private ArrayList<Letter> tray;

    private HashSet<Character>[][] crossChecks;
    private HashMap<Position, Letter> bestMove;
    private String bestWord;
    private int bestScore;

    private Direction direction;
    private final Direction[] allDirections = {Direction.HORIZONTAL, Direction.VERTICAL};

    /**
     * Constructs a Computer player for a Scrabble game.
     * @param game The ScrabbleGame instance that this computer will play
     * @param tray The letters in the computer's hand
     */
    public Computer(ScrabbleGame game, ArrayList<Letter> tray) {
        this.game = game;
        this.tray = tray;
        this.dictionary = game.getDictionary();
        this.boardSize = game.getSize();
        this.bestScore = -1;
        this.crossChecks = new HashSet[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                crossChecks[i][j] = new HashSet<>();
            }
        }
    }

    /**
     * Returns the best word found by the computer.
     * @return The word with the highest score
     */
    public String getBestWord() {
        return bestWord;
    }

    /**
     * Returns the best move found by the computer.
     * @return A HashMap mapping positions to letters for the best move
     */
    public HashMap<Position,Letter> getBestMove() {
        return bestMove;
    }

    /**
     * Returns the score of the best move found by the computer.
     * @return The score of the best move found
     */
    public int getBestScore() {
        return bestScore;
    }

    /**
     * Gets the position before the given position in the current direction.
     * @param p The position to get the position before
     * @return The position before p in the current direction
     */
    private Position before(Position p) {
        if(direction == Direction.HORIZONTAL) {
            return new Position(p.row, p.col-1);
        } else {
            return new Position(p.row-1, p.col);
        }
    }

    /**
     * Gets the position after the given position in the current direction.
     * @param p The position to get the position after
     * @return The position after p in the current direction
     */
    private Position after(Position p) {
        if(direction == Direction.HORIZONTAL) {
            return new Position(p.row, p.col+1);
        } else {
            return new Position(p.row+1, p.col);
        }
    }

    /**
     * Gets the position before the given position in the perpendicular
     * direction.
     * @param p The position to get the position before
     * @return The position before p in the perpendicular direction
     */
    private Position beforeCross(Position p) {
        if(direction == Direction.HORIZONTAL) {
            return new Position(p.row-1, p.col);
        } else {
            return new Position(p.row, p.col-1);
        }
    }

    /**
     * Gets the position after the given position in the perpendicular
     * direction.
     * @param p The position to get the position after
     * @return The position after p in the perpendicular direction
     */
    private Position afterCross(Position p) {
        if(direction == Direction.HORIZONTAL) {
            return new Position(p.row+1, p.col);
        } else {
            return new Position(p.row, p.col+1);
        }
    }

    /**
     * Calculates which letters can be placed at each position on the board
     * without forming invalid cross-words. These constraints are stored in the
     * crossChecks array.
     */
    private void calculateCrossChecks() {
        Tile[][] board = game.getBoard();

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Position current = new Position(row, col);
                if (game.tileIsFilled(current)) {
                    continue;
                }
                HashSet<Character> curCross = new HashSet<>();

                String lettersBefore = "";
                Position scanPos = current;
                while (game.tileIsFilled(beforeCross(scanPos))) {
                    scanPos = beforeCross(scanPos);
                    lettersBefore = board[scanPos.row][scanPos.col].getLetter().
                            toString().toLowerCase() + lettersBefore;
                }
                String lettersAfter = "";
                scanPos = current;
                while (game.tileIsFilled(afterCross(scanPos))) {
                    scanPos = afterCross(scanPos);
                    lettersAfter = lettersAfter + board[scanPos.row][scanPos.col].
                            getLetter().toString().toLowerCase();
                }

                if (lettersBefore.length() == 0 && lettersAfter.length() == 0) {
                    for (char i = 'a'; i <= 'z'; i++) {
                        curCross.add(i);
                    }
                } else {
                    for (char i = 'a'; i <= 'z'; i++) {
                        String word = lettersBefore + i + lettersAfter;
                        if (dictionary.searchForWord(word)) {
                            curCross.add(i);
                        }
                    }
                }

                crossChecks[current.row][current.col] = curCross;
            }
        }
    }

    /**
     * Finds all the anchor positions on the board where a new word could be
     * placed. An anchor is an empty square adjacent to at least one filled
     * square.
     * @return A list of anchor positions
     */
    private ArrayList<Position> findAnchors() {
        ArrayList<Position> anchors = new ArrayList<>();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Position position = new Position(i, j);
                boolean empty = game.tileIsEmpty(position);
                boolean filledNeighbor = game.tileIsFilled(before(position)) ||
                        game.tileIsFilled(after(position)) ||
                        game.tileIsFilled(afterCross(position)) ||
                        game.tileIsFilled(beforeCross(position));

                if(empty && filledNeighbor) {
                    anchors.add(position);
                }
            }
        }
        return anchors;
    }

    /**
     * Extends a word to the after a starting position, checking
     * all possible continuations using letters from the tray.
     * @param startNode The current node in the trie
     * @param pos The current position on the board
     * @param rack The current rack of letters
     * @param word The word built so far
     */
    private void extendAfter(Trie.Node startNode, Position pos, ArrayList<Letter> rack, String word) {
        if (!game.onBoard(pos.row, pos.col)) {
            return;
        }

        if (game.tileIsEmpty(pos)) {
            if (startNode.isWord && word.length() > 1) {
                scoreMove(word, before(pos));
            }

            for (int i = 0; i < rack.size(); i++) {
                Letter letter = rack.get(i);
                char letterChar;
                boolean isBlank = letter.isBlank();

                if (!isBlank) {
                    letterChar = letter.getChar();
                    if (startNode.children.containsKey(letterChar) &&
                            crossChecks[pos.row][pos.col].contains(letterChar)) {
                        // tmep rack
                        ArrayList<Letter> newRack = new ArrayList<>(rack);
                        newRack.remove(i);

                        Trie.Node newNode = startNode.children.get(letterChar);
                        String newWord = word + letterChar;

                        Position nextPos = after(pos);
                        if (game.onBoard(nextPos.row, nextPos.col)) {
                            extendAfter(newNode, nextPos, newRack, newWord);
                        } else if (newNode.isWord) {
                            scoreMove(newWord, pos);
                        }
                    }
                }
                // Handle blank tiles
                else {
                    for (char blankLetter : startNode.children.keySet()) {
                        if (crossChecks[pos.row][pos.col].contains(blankLetter)) {
                            ArrayList<Letter> newRack = new ArrayList<>(rack);
                            newRack.remove(i);

                            Trie.Node newNode = startNode.children.get(blankLetter);
                            // show the blank on testing cases
                            String newWord = word + Character.toUpperCase(blankLetter);

                            Position nextPos = after(pos);
                            if (game.onBoard(nextPos.row, nextPos.col)) {
                                extendAfter(newNode, nextPos, newRack, newWord);
                            } else if (newNode.isWord) {
                                scoreMove(newWord, pos);
                            }
                        }
                    }
                }
            }
        }
        else {
            char tileLetter = Character.toLowerCase(
                    game.getBoard()[pos.row][pos.col].getLetter().getChar()
            );

            if (startNode.children.containsKey(tileLetter)) {
                Trie.Node newNode = startNode.children.get(tileLetter);
                String newWord = word + tileLetter;
                Position nextPos = after(pos);
                if (game.onBoard(nextPos.row, nextPos.col)) {
                    extendAfter(newNode, nextPos, rack, newWord);
                } else if (newNode.isWord) {
                    // last place on board and still a word.
                    scoreMove(newWord, pos);
                }
            }
        }
    }

    /**
     * Generates possible word prefixes that could be placed before an anchor position,
     * then extends them to complete words.
     * @param startNode The current node in the trie
     * @param anchorPos The anchor position
     * @param rack The current rack of letters
     * @param word The word built so far
     * @param limit The maximum number of letters that can be placed before the anchor
     */
    private void beforePart(Trie.Node startNode, Position anchorPos,
                            ArrayList<Letter> rack, String word, int limit) {
        if (!game.onBoard(anchorPos.row, anchorPos.col)) {
            return;
        }
        extendAfter(startNode, anchorPos, rack, word);

        if (limit > 0) {
            for (int i = 0; i < rack.size(); i++) {
                Letter letter = rack.get(i);
                char letterChar;
                boolean isBlank = letter.isBlank();

                if (!isBlank) {
                    letterChar = letter.getChar();
                    if (startNode.children.containsKey(letterChar)) {
                        // temp rack
                        ArrayList<Letter> newRack = new ArrayList<>(rack);
                        newRack.remove(i);

                        Trie.Node newNode = startNode.children.get(letterChar);
                        String newWord = word + letterChar;

                        beforePart(newNode, anchorPos, newRack, newWord, limit - 1);
                    }
                }
                // Handle blank tiles
                else {
                    for (char blankLetter : startNode.children.keySet()) {
                        ArrayList<Letter> newRack = new ArrayList<>(rack);
                        newRack.remove(i);

                        Trie.Node newNode = startNode.children.get(blankLetter);
                        String newWord = word + Character.toUpperCase(blankLetter);

                        beforePart(newNode, anchorPos, newRack, newWord, limit - 1);
                    }
                }
            }
        }
    }

    /**
     * Scores a valid move and updates the best move if this one has a higher score.
     * @param partialWord The word being placed
     * @param end The end position of the word
     */
    private void scoreMove(String partialWord, Position end) {

        HashMap<Position, Letter> move = new HashMap<>();
        LinkedList<Position> positions = new LinkedList<>();
        Position curPos = end;

        for (int i = partialWord.length() - 1; i >= 0; i--) {
            if (!game.onBoard(curPos.row, curPos.col)) {
                return;
            }
            char curChar = partialWord.charAt(i);
            if (game.tileIsEmpty(curPos)) {
                if (Character.isUpperCase(curChar)) {
                    Letter blankTile = new Letter('*');
                    blankTile.setBlank(Character.toLowerCase(curChar));
                    move.put(curPos, blankTile);
                } else {
                    // Regular tile
                    move.put(curPos, new Letter(curChar));
                }
                positions.add(curPos);
            }
            curPos = before(curPos);
        }

        if (positions.isEmpty()) {
            return;
        }


        boolean filledAnchor = false;
        for(Position p: move.keySet()) {
            if(findAnchors().contains(p)) {
                filledAnchor = true;
            }
        }

        if(!filledAnchor) return;

        int score = game.evaluateScore(positions, move);
        if (score > bestScore) {
            bestScore = score;
            bestMove = new HashMap<>(move);
            bestWord = partialWord;
        } else if (score == bestScore) {
            if (partialWord.charAt(0) < bestWord.charAt(0)) {
                bestScore = score;
                bestMove = new HashMap<>(move);
                bestWord = partialWord;
            }
        }
    }

    /**
     * Finds all possible valid words that can be played with the current tray
     * and board state. Updates the bestMove, bestWord, and bestScore fields
     * with the highest-scoring play found.
     */
    public void findAllWords() {
        bestMove = new HashMap<>();
        bestScore = -1;
        bestWord = null;

        for (Direction dir : allDirections) {
            direction = dir;
            calculateCrossChecks();
            List<Position> anchors = findAnchors();
            if (!anchors.isEmpty()) {
                for (Position anchorPos : anchors) {
                    if (game.onBoard(anchorPos.row, anchorPos.col)) {
                        Position beforePos = before(anchorPos);

                        if (game.onBoard(beforePos.row, beforePos.col)
                                && game.tileIsFilled(beforePos)) {
                            Position scanPos = beforePos;
                            StringBuilder prefixBuilder = new StringBuilder();
                            prefixBuilder.append(Character.toLowerCase(
                                    game.getBoard()[scanPos.row][scanPos.col].
                                            getLetter().getChar()));

                            while (game.onBoard(before(scanPos).row, before(scanPos).col) &&
                                    game.tileIsFilled(before(scanPos))) {
                                scanPos = before(scanPos);
                                prefixBuilder.insert(0,
                                        Character.toLowerCase(game.getBoard()
                                                [scanPos.row][scanPos.col].getLetter().getChar()));
                            }

                            String prefix = prefixBuilder.toString();
                            Trie.Node prefixNode = dictionary.getNode(prefix);

                            if (prefixNode != null) {
                                extendAfter(prefixNode, anchorPos, new ArrayList<>(tray), prefix);
                            }
                        } else {

                            // No letters before this anchor, so we can place letters before it
                            int limit = 0;
                            Position scanPos = anchorPos;
                            while (game.onBoard(before(scanPos).row, before(scanPos).col) &&
                                    game.tileIsEmpty(before(scanPos)) &&
                                    !anchors.contains(before(scanPos))) {
                                limit++;
                                scanPos = before(scanPos);
                                if (limit >= tray.size()) {
                                    break;
                                }
                            }

                            // Generate parts that can go before the anchor, then extend them
                            beforePart(dictionary.root, anchorPos, new ArrayList<>(tray), "", limit);
                        }
                    }
                }
            }
            else {
                // first move logic implementation
                Position centerPos = new Position(boardSize / 2, boardSize / 2);
                if (game.onBoard(centerPos.row, centerPos.col)) {
                    int limit = Math.min(tray.size() - 1, boardSize / 2);
                    beforePart(dictionary.root, centerPos, new ArrayList<>(tray), "", limit);
                }
            }
        }

        // if no words are found
        if (bestMove == null) {
            bestMove = new HashMap<>();
        }
    }
}