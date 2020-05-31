package controller;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import model.Cell;
import model.SpreadSheet;
import view.CellSheetView;

public class CellSheetController {

    private int row = 0;
    private int col = 0;

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
        Platform.runLater(() -> move(0,0));
    }

    public void move(int dRow, int dCol) {
        row = Math.max(0, Math.min(row+dRow, getMaxRow()-1));
        col = Math.max(0, Math.min(col+dCol, getMaxColumn()-1));

        view.setFocusedCell();
        view.getParentView().getFormView().setCell(getCell(row, col));
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public void setCurrentCell(Cell cell) {
        row = cell.getRow();
        col = cell.getColumn();
        view.getParentView().getFormView().setCell(cell);
    }
    public int getMaxRow() { return SpreadSheet.MAX_ROW; }
    public int getMaxColumn() { return SpreadSheet.MAX_COLUMN; }
    public DoubleProperty getHeightProperty(int row) {
        return model.getRowHeader().get(row).heightProperty();
    }
    public DoubleProperty getWidthProperty(int col) {
        return model.getColumnHeader().get(col).widthProperty();
    }
    public ObjectProperty<Pos> getPosProperty(int row, int col) {
        return model.getCell(row, col).posProperty();
    }
    public Cell getCell(int row, int col) {
        return model.getCell(row, col);
    }
    public String getValue(int row, int col) {
        return getCell(row, col).getValue();
    }
    public String getCurrentValue() {
        return getValue(getRow(), getCol());
    }
    public String getId(int row, int col) {
        return getCell(row, col).getId();
    }
    public String getCurrentId() {
        return getCell(row, col).getId();
    }

}
