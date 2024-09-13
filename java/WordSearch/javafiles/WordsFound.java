package javafiles;
public class WordsFound {
    private String word;
    private int row;
    private int col;
    private Direction direction;

    /**
     * Words found constuctor method
     * @param word Word found
     * @param row Which row the word was found at
     * @param col Which col the word was found at
     * @param direction which direction the word appears
     */
    public WordsFound (String word, int row, int col, Direction direction) {
        this.word = word;
        this.row = row;
        this.col = col;
        this.direction = direction;
    }

    /**
     * Getter method for word variable in WordsFound
     * @return string
     */
    public String getWord () {
        return this.word;
    }

    /**
     * Getter method for row variable in WordsFound
     * @return int for row
     */
    public int getRow () {
        return this.row;
    }

    /**
     * Getter method for col variable in WordsFound
     * @return int for column
     */
    public int getCol () {
        return this.col;
    }

    /**
     * Getter method for direction enum in WordsFound
     * @return direction enum
     */
    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public String toString() {
        return this.word + " " + this.row
                + " " + this.col + " " + this.direction;
    }
}
