// Coordinate of Node in state
public class Location {
    private int row;
    private int column;

    public Location(int r, int c) {
        this.row = r;
        this.column = c;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public String toString() {
        return "(" + String.valueOf(row) + ", " + String.valueOf(column) + ")";
    }
}
