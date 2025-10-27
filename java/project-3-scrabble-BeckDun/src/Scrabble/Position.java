/**
 * Position class used in the scrabble game.
 * This class hold a row value and a column value.
 * @author BeckDun
 */
package Scrabble;

public class Position {
    public final int row;
    public final int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Position(int[] position) {
        this.row = position[0];
        this.col = position[1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;
        return row == position.row && col == position.col;
    }
}