package controller;

import javafx.beans.property.DoubleProperty;
import model.Cell;
import model.SpreadSheet;

public class CellSheetController {

    private int row;
    private int col;

    private final SpreadSheet model;

    public CellSheetController(SpreadSheet model) {
        this.model = model;
        row = 0;
        col = 0;
    }

    public void move(int dRow, int dCol) {
        int oldRow = row;
        int oldCol = col;
        row = Math.max(0, Math.min(row+dRow, SpreadSheet.MAX_ROW-1));
        col = Math.max(0, Math.min(col+dCol, SpreadSheet.MAX_COLUMN-1));

        /*if (dRow != 0 && oldRow == row)
            //view.moveScrollBar(0, dRow);
        else if (dCol != 0 && oldCol == col)
            //view.moveScrollBar(dCol, 0);
        else {
            //view.setCellFocus();
            //view.updateForm();
        }*/
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getMaxRow() { return SpreadSheet.MAX_ROW; }
    public int getMaxColumn() { return SpreadSheet.MAX_COLUMN; }
    public DoubleProperty getHeightProperty(int row) {
        return model.getRowHeader().get(row).heightProperty();
    }
    public DoubleProperty getWidthProperty(int col) {
        return model.getColumnHeader().get(col).widthProperty();
    }
    public Cell getCell(int row, int col) {
        return model.getCell(row, col);
    }
    public String getValue(int row, int col) {
        return getCell(row, col).getValue();
    }
    public String getId(int row, int col) {
        return getCell(row, col).getId();
    }
    public String getCurrentId() {
        return getCell(row, col).getId();
    }

}
