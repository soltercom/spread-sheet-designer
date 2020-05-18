package model;

public class Cell {

    private final int row;
    public int getRow() {
        return row;
    }
    private final int column;
    public int getColumn() {
        return column;
    }
    private String value;
    public String getValue() {
        return value;
    }
    private CellType type;

    public Cell(int row, int column, String value, CellType type) {
        this.row = row;
        this.column = column;
        this.value = value;
        this.type = type;
    }

    public Cell(int row, int column) {
        this(row, column, "", CellType.TEXT);
    }

}
