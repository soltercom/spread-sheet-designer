package model;

public class Area {

    public static Area EMPTY_AREA = new Area(-10, -10);

    private final int startRow, endRow, startColumn, endColumn;

    public Area(int row, int column) {
        startRow = row;
        endRow   = row;
        startColumn = column;
        endColumn = column;
    }

    public Area(int startRow, int endRow, int startColumn, int endColumn) {
        this.startRow = startRow;
        this.endRow = endRow;
        this.startColumn = startColumn;
        this.endColumn = endColumn;
    }

    public Area setRange(int row, int col) {
        if (getStartRow() < 0 || getStartColumn() < 0)
            return new Area(row, row, col, col);
        else if (row < getStartRow() || col < getStartColumn())
            return new Area(row, row, col, col);
        else
            return new Area(getStartRow(), row, getStartColumn(), col);
    }

    public int getStartRow() {
        return startRow;
    }
    public int getEndRow() {
        return endRow;
    }
    public int getStartColumn() {
        return startColumn;
    }
    public int getEndColumn() {
        return endColumn;
    }

    @Override
    public String toString() {
        return "Area{" +
                "startRow=" + startRow +
                ", endRow=" + endRow +
                ", startColumn=" + startColumn +
                ", endColumn=" + endColumn +
                '}';
    }
}
