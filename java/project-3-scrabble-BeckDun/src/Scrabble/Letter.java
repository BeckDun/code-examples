
package Scrabble;
/**
 * Letter class to be used in scrabble.
 * Each letter holds a character and a point value.
 * @author BeckDun
 */
public class Letter{
    private Character letter;
    private Character blankSet;
    private int points;
    private boolean blank = false;

    /**
     * Full constructor for letters to associate characters and points
     * @param letter character to associate with letter
     * @param points points the letter is worth
     */
    public Letter(Character letter, int points) {
        if(letter == '*') {
            blank = true;
            blankSet = '*';
        }
            this.letter = letter;
            this.points = points;
    }

    /**
     * Constructor for letters that associates the letter with points.
     * This is used when there is not a file specifying the distribution and
     * points for each letter
     * @param letter character to associate with the letter object
     */
    public Letter(Character letter) {
        this.letter = letter;
        switch (letter) {
            case 'a': this.points = 1; break;
            case 'b': this.points = 3; break;
            case 'c': this.points = 3; break;
            case 'd': this.points = 2; break;
            case 'e': this.points = 1; break;
            case 'f': this.points = 4; break;
            case 'g': this.points = 2; break;
            case 'h': this.points = 4; break;
            case 'i': this.points = 1; break;
            case 'j': this.points = 8; break;
            case 'k': this.points = 5; break;
            case 'l': this.points = 1; break;
            case 'm': this.points = 3; break;
            case 'n': this.points = 1; break;
            case 'o': this.points = 1; break;
            case 'p': this.points = 3; break;
            case 'q': this.points = 10; break;
            case 'r': this.points = 1; break;
            case 's': this.points = 1; break;
            case 't': this.points = 1; break;
            case 'u': this.points = 1; break;
            case 'v': this.points = 4; break;
            case 'w': this.points = 4; break;
            case 'x': this.points = 8; break;
            case 'y': this.points = 4; break;
            case 'z': this.points = 10; break;
            case'*':
                this.points = 0;
                this.blank = true;
                this.blankSet = '*';
                break;
        }
    }


    /**
     * toString for a letter. prints out the character associated with a letter
     * @return string of the character the letter holds
     */
    @Override
    public String toString() {
        if(blank) {
            return blankSet.toString();
        }
        return Character.toString(letter);
    }

    /**
     * Checks for equality with another Letter. Only checks the characters, not
     * the points for a letter
     * @param obj object to check equality against
     * @return true if the letters have matching characters
     */
    @Override
    public boolean equals(Object obj) {
        Letter check = (Letter)obj;

        return this.letter == check.getChar();
    }

    /**
     * getter method for the points of a given letter
     * @return number of points
     */
    public int getPoints() {
        return points;
    }

    /**
     * getter method for the character associated with a letter
     * @return character of the letter
     */
    public Character getChar() {
        if(blank) {
            return blankSet;
        }
        return this.letter;
    }

    /**
     * If letter is a blank, we set the letter to a temporary letter
     * @param letter character to set our letter to
     */
    public void setBlank(Character letter) {
        if(blank) {
            blankSet = Character.toUpperCase(letter);
        }
    }

    /**
     * Checks is the letter is a blank
     * @return true if the letter is blank
     */
    public boolean isBlank() {
        return blank;
    }
}
