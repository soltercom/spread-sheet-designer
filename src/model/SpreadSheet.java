package model;

public class SpreadSheet {

    public static final int MAX_COLUMN = 45;
    public static final int MAX_ROW = 70;

    private final ColumnHeader columnHeader = new ColumnHeader();
    private final RowHeader rowHeader = new RowHeader();
    private final Cell[][] cells = new Cell[MAX_ROW][MAX_COLUMN];

    public SpreadSheet() {
        init();
    }

    private void init() {
        for (int i = 0; i < MAX_COLUMN; i++)
            columnHeader.add(new Column(i+1));
        for (int i = 0; i < MAX_ROW; i++)
            rowHeader.add(new Row(i+1));
        for (int i = 0; i < MAX_ROW; i++)
            for (int j = 0; j < MAX_COLUMN; j++)
                cells[i][j] = new Cell(i, j, "", CellType.TEXT, new CellBorders());
    }

    public ColumnHeader getColumnHeader() {
        return columnHeader;
    }

    public RowHeader getRowHeader() {
        return rowHeader;
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }
}
