package model;

public class SpreadSheet {

    public final int MAX_COLUMN = 45;
    public final int MAX_ROW = 75;

    private final Cell[][] cells = new Cell[MAX_ROW][MAX_COLUMN];

    public SpreadSheet() {
        init();
    }

    private void init() {
        for (int i = 0; i < MAX_ROW; i++) {
            for (int j = 0; j < MAX_COLUMN; j++) {
                cells[i][j] = new Cell(i, j, (i+1) + ":" + (j+1), CellType.TEXT);
            }
        }
    }

    public String getCellText(int row, int col) {
        return cells[row][col].getValue();
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }
}
