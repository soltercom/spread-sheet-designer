package view;

import controller.SpreadSheetController;
import form.Form;
import javafx.beans.property.DoubleProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import model.Cell;

public class SpreadSheetView extends GridPane {

    private static final double CELL_WIDTH = 75.0D;
    private static final double CELL_HEIGHT = 25.0D;
    private static final double SCROLL_BAR_WIDTH = 20.0D;
    private static final double FORM_WIDTH = 200.0D;
    public static final int MAX_COLUMN = 15;
    public static final int MAX_ROW = 25;

    private static final PseudoClass EDIT_CELL_CLASS = PseudoClass.getPseudoClass("edit");

    private final SpreadSheetController controller;

    private ScrollBar hScrollBar = new ScrollBar();
    private ScrollBar vScrollBar = new ScrollBar();

    private TextField[] columnHeaders = new TextField[MAX_COLUMN];
    private TextField[] rowHeaders    = new TextField[MAX_ROW];
    private TextField[][] cells       = new TextField[MAX_ROW][MAX_COLUMN];

    private Form form;

    public SpreadSheetView() {
        form = new Form(this);
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

        getColumnConstraints().add(new ColumnConstraints(FORM_WIDTH));
        add(form, MAX_COLUMN + 2, 0, 1, MAX_ROW + 1);
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
        updateForm();
    }

    public void beforeEditCell() {
        TextField cell = cells[controller.getRow()][controller.getCol()];
        cell.pseudoClassStateChanged(EDIT_CELL_CLASS, true);
    }

    public void afterEditCell() {
        TextField cell = cells[controller.getRow()][controller.getCol()];
        cell.setText(controller.getCellText(controller.getRow(), controller.getCol()));
        cell.requestFocus();
        cell.pseudoClassStateChanged(EDIT_CELL_CLASS, false);
    }

    public void setCellFocus() {
        TextField cell = cells[controller.getRow()][controller.getCol()];
        cell.requestFocus();
    }

    public void updateForm() {
        TextField cell = cells[controller.getRow()][controller.getCol()];
        form.setCell((Cell)cell.getUserData());
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
