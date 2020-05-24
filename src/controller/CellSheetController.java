package controller;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import model.Cell;
import model.SpreadSheet;
import view.CellSheetView;

public class CellSheetController {

    private int row;
    private int col;

    private final SpreadSheet model;
    private final CellSheetView view;

    public CellSheetController(SpreadSheet model, CellSheetView view) {
        this.model = model;
        this.view = view;
        init();
    }

    private void init() {
        row = 0;
        col = 0;
        Platform.runLater(view::setFocusedCell);
    }

    public void move(int dRow, int dCol) {
        int oldRow = row;
        int oldCol = col;
        row = Math.max(0, Math.min(row+dRow, getMaxRow()-1));
        col = Math.max(0, Math.min(col+dCol, getMaxColumn()-1));

        view.setFocusedCell();
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public void setCurrentCell(Cell cell) {
        row = cell.getRow();
        col = cell.getColumn();
    }
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
