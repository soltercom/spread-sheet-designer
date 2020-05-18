package view;

import controller.SpreadSheetController;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import model.Cell;

import java.util.Objects;

public class SpreadSheetView extends GridPane {

    private static final double CELL_WIDTH = 75.0D;
    private static final double CELL_HEIGHT = 25.0D;
    private static final double SCROLL_BAR_WIDTH = 20.0D;
    public static final int MAX_COLUMN = 15;
    public static final int MAX_ROW = 25;

    private final SpreadSheetController controller;

    private ScrollBar hScrollBar = new ScrollBar();
    private ScrollBar vScrollBar = new ScrollBar();

    private TextField[] columnHeaders = new TextField[MAX_COLUMN];
    private TextField[] rowHeaders    = new TextField[MAX_ROW];
    private TextField[][] cells       = new TextField[MAX_ROW][MAX_COLUMN];

    public SpreadSheetView() {
        initView();
        controller = new SpreadSheetController(this);
    }

    private TextField createTextField() {
        var textField = new TextField();
        textField.setAlignment(Pos.CENTER);
        textField.setEditable(false);
        return textField;
    }

    private void initView() {
        for (int i = 0; i <= MAX_COLUMN; i++)
            getColumnConstraints().add(new ColumnConstraints(CELL_WIDTH));
        getColumnConstraints().add(new ColumnConstraints(SCROLL_BAR_WIDTH));
        for (int i = 0; i <= MAX_ROW; i++)
            getRowConstraints().add(new RowConstraints(CELL_HEIGHT + 1.0));
        getRowConstraints().add(new RowConstraints(SCROLL_BAR_WIDTH));

        hScrollBar = new ScrollBar();
        hScrollBar.setOrientation(Orientation.HORIZONTAL);
        hScrollBar.setMin(0.0D);
        hScrollBar.setMax(2.0D * MAX_COLUMN);
        hScrollBar.setUnitIncrement(1.0D);
        hScrollBar.setBlockIncrement(1.0D);
        add(hScrollBar, 0, MAX_ROW + 1, MAX_COLUMN + 1, 1);

        vScrollBar = new ScrollBar();
        vScrollBar.setOrientation(Orientation.VERTICAL);
        vScrollBar.setMin(0.0D);
        vScrollBar.setMax(2.0D * MAX_ROW);
        vScrollBar.setUnitIncrement(1.0D);
        vScrollBar.setBlockIncrement(1.0D);
        add(vScrollBar, MAX_COLUMN + 1, 0, 1, MAX_ROW + 1);

        add(createTextField(), 0, 0);
        for (int i = 0; i < MAX_COLUMN; i++) {
            columnHeaders[i] = createTextField();
            columnHeaders[i].getStyleClass().add("column-header");
            add(columnHeaders[i], i+1, 0);
        }
        for (int i = 0; i < MAX_ROW; i++) {
            rowHeaders[i]=createTextField();
            rowHeaders[i].getStyleClass().add("row-header");
            add(rowHeaders[i], 0, i+1);
        }

        for (int i = 0; i < MAX_ROW; i++)
            for (int j = 0; j < MAX_COLUMN; j++) {
                cells[i][j] = createTextField();
                cells[i][j].getStyleClass().add("cell");
                cells[i][j].setOnKeyPressed(this::onCellKeyPressed);
                cells[i][j].setOnMousePressed(this::onCellMousePressed);
                add(cells[i][j], j+1, i+1);
            }

    }

    private void onCellMousePressed(MouseEvent event) {
        controller.moveTo((Cell)((TextField)event.getSource()).getUserData());
    }

    private void onCellKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.UP)
            controller.move(-1, 0);
        else if (event.getCode() == KeyCode.RIGHT)
            controller.move(0, 1);
        else if (event.getCode() == KeyCode.DOWN)
            controller.move(1, 0);
        else if (event.getCode() == KeyCode.LEFT)
            controller.move(0, -1);
    }

    public void redraw() {

        for (int i = 0; i < MAX_COLUMN; i++)
            columnHeaders[i].setText(controller.getColumnHeaderText(i));

        for (int i = 0; i < MAX_ROW; i++)
            rowHeaders[i].setText(controller.getRowHeaderText(i));

        for (int row = 0; row < MAX_ROW; row++)
            for (int col = 0; col < MAX_COLUMN; col++) {
                cells[row][col].setText(controller.getCellText(row, col));
                cells[row][col].setUserData(controller.getCell(row, col));
            }

        setCellFocus();

    }

    public void setCellFocus() {
        cells[controller.getRow()][controller.getCol()].requestFocus();
    }

    public void moveScrollBar(int dH, int dV) {
             if (dH > 0 && hScrollBar.getValue() < hScrollBar.getMax())
            hScrollBar.setValue(hScrollBar.getValue() + 1.0);
        else if (dH < 0 && hScrollBar.getValue() > hScrollBar.getMin())
            hScrollBar.setValue(hScrollBar.getValue() - 1.0);

             if (dV > 0 && vScrollBar.getValue() < vScrollBar.getMax())
            vScrollBar.setValue(vScrollBar.getValue() + 1.0);
        else if (dV < 0 && vScrollBar.getValue() > vScrollBar.getMin())
            vScrollBar.setValue(vScrollBar.getValue() - 1.0);
    }

    public DoubleProperty getHScrollValueProperty() {
        return hScrollBar.valueProperty();
    }
    public DoubleProperty getVScrollValueProperty() {
        return vScrollBar.valueProperty();
    }

    @Override
    public String getUserAgentStylesheet() {
        return SpreadSheetView.class.getResource("style.css").toExternalForm();
    }

}
