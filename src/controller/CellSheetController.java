package controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import model.Area;
import model.Cell;
import model.SpreadSheet;
import view.CellSheetView;
import view.SpreadSheetView;

import java.util.ArrayList;
import java.util.List;

public class CellSheetController {

    private int row = 0;
    private int col = 0;

    private final SpreadSheet model;
    private final CellSheetView view;

    private Area selectedArea;
    private List<Area> areaList = new ArrayList<>();

    public CellSheetController(SpreadSheet model, CellSheetView view) {
        this.model = model;
        this.view = view;
        init();
        unsetSelectedArea();
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

        view.getParentView().focusedViewPartProperty().setValue(SpreadSheetView.ViewParts.CELL_SHEET);
    }

    public void createArea() {
        areaList.add(selectedArea);
        view.redrawArea(selectedArea, true);
        unsetSelectedArea();
    }

    private void updateSelectedAreaView(boolean state) {
        if (selectedArea == null || selectedArea.getStartRow() < 0 || selectedArea.getStartColumn() < 0) return;
        for (int row = selectedArea.getStartRow(); row <= selectedArea.getEndRow() ; row++)
            for (int col = selectedArea.getStartColumn(); col <= selectedArea.getEndColumn() ; col++)
                view.updatePseudoClassSelectedArea(row, col, state);
    }

    public void unsetSelectedArea() {
        updateSelectedAreaView(false);
        selectedArea = Area.EMPTY_AREA;
    }

    public void setSelectedArea(Cell cell) {
        updateSelectedAreaView(false);
        selectedArea = selectedArea.setRange(cell.getRow(), cell.getColumn());
        updateSelectedAreaView(true);
    }

    public boolean isCellInSelectedArea(Cell cell) {
        return selectedArea.getStartRow() <= cell.getRow()
            && cell.getRow() <= selectedArea.getEndRow()
            && selectedArea.getStartColumn() <= cell.getColumn()
            && cell.getColumn() <= selectedArea.getEndColumn();
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public void setCurrentCell(Cell cell) {
        row = cell.getRow();
        col = cell.getColumn();
        view.getParentView().getFormView().setCell(cell);

        view.getParentView().focusedViewPartProperty().setValue(SpreadSheetView.ViewParts.CELL_SHEET);
    }
    public int getMaxRow() { return SpreadSheet.MAX_ROW; }
    public int getMaxColumn() { return SpreadSheet.MAX_COLUMN; }
    public DoubleProperty getHeightProperty(int row) {
        return model.getRowHeader().getLengthProperty(row);
    }
    public DoubleBinding getHeightProperty(int startRow, int endRow) {
        DoubleBinding height = Bindings.createDoubleBinding(() -> -1.0D);
        for (int row = startRow; row <= endRow ; row++) {
            height = height.add(getHeightProperty(row)).add(1.0D);
        }
        return height;
    }
    public DoubleProperty getWidthProperty(int col) {
        return model.getColumnHeader().getLengthProperty(col);
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
    public boolean hasSelectedArea() {
        return selectedArea != null && selectedArea.getStartRow() >= 0;
    }

}
