package controller;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import model.Cell;
import model.SpreadSheet;
import view.SpreadSheetView;

public class SpreadSheetController {

    private final SpreadSheet model;
    private final SpreadSheetView view;

    private int leftPosition;
    private int topPosition;

    public SpreadSheetController(SpreadSheetView view) {
        model = new SpreadSheet();
        this.view = view;
        setBindings();
    }

    public String getColumnHeaderText(int col) {
        return String.valueOf(col+leftPosition+1);
    }

    public String getRowHeaderText(int row) {
        return String.valueOf(row+topPosition+1);
    }

    public String getCellText(int row, int col) {
        return model.getCellText(row+topPosition, col+leftPosition);
    }

    private void setBindings() {
        view.getHScrollValueProperty().addListener((o, oldValue, newValue) -> {
            leftPosition = newValue.intValue();
            view.redraw();
        });
        view.getVScrollValueProperty().addListener((o, oldValue, newValue) -> {
            topPosition = newValue.intValue();
            view.redraw();
        });
    }

}
