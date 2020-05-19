package controller;

import javafx.application.Platform;
import model.Cell;
import model.SpreadSheet;
import view.SpreadSheetView;

public class SpreadSheetController {

    private final SpreadSheet model;
    private final SpreadSheetView view;

    private int row;
    private int col;
    private int hScrollPosition;
    private int vScrollPosition;

    public SpreadSheetController(SpreadSheetView view) {
        model = new SpreadSheet();
        this.view = view;
        setBindings();
        Platform.runLater(view::redraw);
    }

    private void setBindings() {
        view.getHScrollValueProperty().addListener((o, oldValue, newValue) -> {
            hScrollPosition = newValue.intValue();
            view.redraw();
        });
        view.getVScrollValueProperty().addListener((o, oldValue, newValue) -> {
            vScrollPosition = newValue.intValue();
            view.redraw();
        });
    }

    public void move(int dRow, int dCol) {
        int oldRow = row;
        int oldCol = col;
        row = Math.max(0, Math.min(row+dRow, SpreadSheetView.MAX_ROW-1));
        col = Math.max(0, Math.min(col+dCol, SpreadSheetView.MAX_COLUMN-1));

        if (dRow != 0 && oldRow == row)
            view.moveScrollBar(0, dRow);
        else if (dCol != 0 && oldCol == col)
            view.moveScrollBar(dCol, 0);
        else {
            view.setCellFocus();
            view.updateForm();
        }

    }
    public void moveTo(Cell cell) {
        if (cell != null) {
            row = cell.getRow()-vScrollPosition;
            col = cell.getColumn()-hScrollPosition;
            view.updateForm();
        }
    }

    public String getColumnHeaderText(int col) {
        return String.valueOf(col+hScrollPosition+1);
    }
    public String getRowHeaderText(int row) {
        return String.valueOf(row+vScrollPosition+1);
    }
    public String getCellText(int row, int col) {
        return model.getCellText(row+vScrollPosition, col+hScrollPosition);
    }
    public Cell getCell(int row, int col) {
        return model.getCell(row+vScrollPosition, col+hScrollPosition);
    }
    public int getRow() { return row; }
    public int getCol() { return col; }

}
