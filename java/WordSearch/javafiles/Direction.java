package javafiles;
/**
 * Direction enum that is used for moving in a direction on a 2D array.
 */
public enum Direction {
    E(0,1),
    SE(1,1),
    S(1,0),
    SW(1,-1),
    W(0,-1),
    NW(-1,-1),
    N(-1,0),
    NE(-1,1);


    public final int rowOffset;
    public final int colOffset;

    private Direction(int rowOffset ,int colOffset) {
        this.rowOffset = rowOffset;
        this.colOffset = colOffset;
        }
    public static final Direction[] ALL = Direction.values();
    public static final Direction[] FORWARD = {E,SE,S,NE};
    public static final Direction[] HORIZVERT = {N,S,E,W};
    public static final Direction[] DIAGONAL = {NE,SE,SW,SE};

}
