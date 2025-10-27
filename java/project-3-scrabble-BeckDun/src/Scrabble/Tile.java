
package Scrabble;
/**
 * Tile class which holds a letter and point multipliers.
 * To be used with the ScrabbleGame class.
 * @author BeckDun
 */
public class Tile {
    private int wordMult = 1;
    private int letterMult = 1;
    private Letter letter;

    /**
     * Creates a tile with specified word and letter multipliers.
     * The tile is initially empty (has no letter).
     * @param wordMult The word multiplier value
     * @param letterMult The letter multiplier value
     */
    public Tile(int wordMult, int letterMult) {
        this.wordMult = wordMult;
        this.letterMult = letterMult;
        letter = new Letter('\0',0);
    }

    /**
     * Creates a tile with a specified letter.
     * The tile has default multipliers (1).
     * @param letter The letter to place on this tile
     */
    public Tile(Letter letter) {
        this.letter = letter;
    }

    /**
     * getter method for the letter multiplier
     * @return letter multiplier as an int
     */
    public int getLetterMult() {
        return letterMult;
    }

    /**
     * Sets the letter multiplier for the tile
     * @param letterMult int to set the multiplier to
     */
    public void setLetterMult(int letterMult) {
        this.letterMult = letterMult;
    }


    /**
     * getter method for the letter
     * @return Letter on the tile
     */
    public Letter getLetter() {
        return letter;
    }

    /**
     * Sets the letter on the tile
     * @param letter Letter object to set the tiles letter to
     */
    public void setLetter(Letter letter) {
        this.letter = letter;
    }

    /**
     * Removes the letter on the tile.
     * @return Letter removed from the tile
     */
    public Letter removeLetter() {
        Letter temp = this.letter;
        this.letter = new Letter('\0',0);
        return temp;
    }

    /**
     * getter method for the word multiplier
     * @return word multiplier as an int
     */
    public int getWordMult() {
        return wordMult;
    }

    /**
     * setter method for the word multiplier
     * @param wordMult int to set the word multiplier to
     */
    public void setWordMult(int wordMult) {
        this.wordMult = wordMult;
    }

    /**
     * method to check if a tile has a blank letter on it
     * @return true if the tile has a letter with the null character
     */
    public boolean isEmpty() {
        if(this.letter.getChar() == '\0') {
            return true;
        }
        return false;
    }

    /**
     * Returns a string representation of the tile.
     * If the tile has a letter, returns the letter.
     * If the tile is empty, returns a representation of the multipliers.
     * @return String representation of the tile
     */
    @Override
    public String toString() {
        if(letter.getChar() != '\0'){
            return " " + letter;
        } else {
            if(wordMult == 1 && letterMult == 1) {
                return "..";
            } else if(wordMult == 1 && letterMult != 1) {
                return "."+letterMult;
            } else if(wordMult !=1 && letterMult == 1) {
                return wordMult + ".";
            } else {
                return wordMult + "" +letterMult;
            }
        }
    }

    /**
     * Checks if this tile is equal to another object.
     * Two tiles are equal if they have the same letter, letter multiplier,
     * and word multiplier.
     * @param object The object to compare with
     * @return true if the tiles are equal, false otherwise
     */
    @Override
    public boolean equals(Object object) {
        boolean sameLetter = letter.equals(((Tile) object).getLetter());
        boolean sameLetterMult = letterMult == ((Tile) object).getLetterMult();
        boolean sameWordMult = wordMult == ((Tile) object).getWordMult();
        return sameLetterMult && sameWordMult && sameLetter;
    }
}