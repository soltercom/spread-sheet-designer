package view;

import controller.SpreadSheetController;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import model.Cell;
import model.SpreadSheet;


public class SpreadSheetView {

    private static final double CELL_WIDTH = 75.0D;
    private static final double CELL_HEIGHT = 25.0D;
    private static final double SCROLL_BAR_WIDTH = 20.0D;
    public static final int MAX_COLUMN = 15;
    public static final int MAX_ROW = 25;

    private GridPane view;
    public GridPane getView() {
        return this.view;
    }

    private final SpreadSheetController controller;

    private ScrollBar hScrollBar = new ScrollBar();
    private ScrollBar vScrollBar = new ScrollBar();

    private TextField[] columnHeaders = new TextField[MAX_COLUMN];
    private TextField[] rowHeaders    = new TextField[MAX_ROW];
    private TextField[][] cells       = new TextField[MAX_ROW][MAX_COLUMN];

    public SpreadSheetView() {
        initView();
        controller = new SpreadSheetController(this);
        redraw();
    }

    private TextField createTextField() {
        var textField = new TextField();
        textField.setAlignment(Pos.CENTER);
        textField.setEditable(false);
        return textField;
    }

    private void initView() {
        view = new GridPane();

        for (int i = 0; i <= MAX_COLUMN; i++)
            view.getColumnConstraints().add(new ColumnConstraints(CELL_WIDTH));
        view.getColumnConstraints().add(new ColumnConstraints(SCROLL_BAR_WIDTH));
        for (int i = 0; i <= MAX_ROW; i++)
            view.getRowConstraints().add(new RowConstraints(CELL_HEIGHT));
        view.getRowConstraints().add(new RowConstraints(SCROLL_BAR_WIDTH));

        hScrollBar = new ScrollBar();
        hScrollBar.setOrientation(Orientation.HORIZONTAL);
        hScrollBar.setMin(0.0D);
        hScrollBar.setMax(2.0D * MAX_COLUMN);
        hScrollBar.setUnitIncrement(1.0D);
        hScrollBar.setBlockIncrement(1.0D);
        view.add(hScrollBar, 0, MAX_ROW + 1, MAX_COLUMN + 1, 1);

        vScrollBar = new ScrollBar();
        vScrollBar.setOrientation(Orientation.VERTICAL);
        vScrollBar.setMin(0.0D);
        vScrollBar.setMax(2.0D * MAX_ROW);
        vScrollBar.setUnitIncrement(1.0D);
        vScrollBar.setBlockIncrement(1.0D);
        view.add(vScrollBar, MAX_COLUMN + 1, 0, 1, MAX_ROW + 1);

        view.add(createTextField(), 0, 0);
        for (int i = 0; i < MAX_COLUMN; i++)
            view.add(columnHeaders[i] = createTextField(), i+1, 0);
        for (int i = 0; i < MAX_ROW; i++)
            view.add(rowHeaders[i]=createTextField(), 0, i+1);
        for (int i = 0; i < MAX_ROW; i++)
            for (int j = 0; j < MAX_COLUMN; j++)
                view.add(cells[i][j] = createTextField(), j+1, i+1);

    }

    public void redraw() {

        for (int i = 0; i < MAX_COLUMN; i++)
            columnHeaders[i].setText(controller.getColumnHeaderText(i));

        for (int i = 0; i < MAX_ROW; i++)
            rowHeaders[i].setText(controller.getRowHeaderText(i));

        for (int row = 0; row < MAX_ROW; row++)
            for (int col = 0; col < MAX_COLUMN; col++)
                cells[row][col].setText(controller.getCellText(row, col));

    }

    public DoubleProperty getHScrollValueProperty() {
        return hScrollBar.valueProperty();
    }
    public DoubleProperty getVScrollValueProperty() {
        return vScrollBar.valueProperty();
    }
}
