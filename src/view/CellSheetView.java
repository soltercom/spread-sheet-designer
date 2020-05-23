package view;

import controller.CellSheetController;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;

public class CellSheetView extends GridPane {

    private static final double SPREAD_SHEET_HEIGHT = 600.0D;
    private static final double SPREAD_SHEET_WIDTH = 1000.0D;
    private static final double BORDER_WIDTH = 1.0D;

    private final ScrollBar hScrollBar;
    private final ScrollBar vScrollBar;

    private final CellSheetController controller;

    public CellSheetView(CellSheetController controller, ScrollBar hScrollBar, ScrollBar vScrollBar) {
        this.controller = controller;
        this.hScrollBar = hScrollBar;
        this.vScrollBar = vScrollBar;
        init();
        setBindings();
    }

    private void init() {
        getRowConstraints().add(new RowConstraints(BORDER_WIDTH));
        for (int i = 0; i < controller.getMaxRow(); i++) {
            addRowConstraint(i);
            getRowConstraints().add(new RowConstraints(BORDER_WIDTH));
        }
        getColumnConstraints().add(new ColumnConstraints(BORDER_WIDTH));
        for (int i = 0; i < controller.getMaxColumn(); i++) {
            addColumnConstraint(i);
            getColumnConstraints().add(new ColumnConstraints(BORDER_WIDTH));
        }

        for (int i = 0; i < controller.getMaxRow(); i++)
            add(createVLine(i, 0), 0, i*2+1, 1, 1);

        for (int i = 0; i < controller.getMaxColumn(); i++)
            add(createHLine(0, i), i*2+1, 0, 1, 1);

        for (int i = 0; i < controller.getMaxRow(); i++) {
            for (int j = 0; j < controller.getMaxColumn(); j++) {
                add(createCell(i, j), j*2+1, i*2+1, 1, 1);
                add(createHLine(i, j), j*2+1, i*2+2, 1, 1);
                add(createVLine(i, j), j*2+2, i*2+1, 1, 1);
            }
        }

        redraw();
    }

    public void setFocusCell() {
        if (getScene() != null)
            getScene().lookup("#" + controller.getCurrentId()).requestFocus();
    }

    private void setBindings() {
        hScrollBar.valueProperty().addListener((o, v1, v2) -> redraw());
        vScrollBar.valueProperty().addListener((o, v1, v2) -> redraw());
    }

    public void redraw() {
        double x = hScrollBar.valueProperty().doubleValue();
        double y = vScrollBar.valueProperty().doubleValue();
        setClip(new Rectangle(x, y, SPREAD_SHEET_WIDTH, SPREAD_SHEET_HEIGHT));
        setTranslateX(-x);
        setTranslateY(-y);
        setFocusCell();
    }

    private void onCellMousePressed(MouseEvent event) {

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

    private void addRowConstraint(int row) {
        DoubleProperty heightProperty = controller.getHeightProperty(row);
        RowConstraints constraints = new RowConstraints(heightProperty.get());
        constraints.prefHeightProperty().bind(heightProperty);
        constraints.minHeightProperty().bind(heightProperty);
        constraints.maxHeightProperty().bind(heightProperty);
        getRowConstraints().add(constraints);
    }

    private void addColumnConstraint(int col) {
        DoubleProperty widthProperty = controller.getWidthProperty(col);
        ColumnConstraints constraints = new ColumnConstraints(widthProperty.get());
        constraints.prefWidthProperty().bind(widthProperty);
        constraints.minWidthProperty().bind(widthProperty);
        constraints.maxWidthProperty().bind(widthProperty);
        getColumnConstraints().add(constraints);
    }

    TextField createCell(int row, int col) {
        TextField textField = new TextField(controller.getValue(row, col));
        textField.maxHeightProperty().bind(controller.getHeightProperty(row));
        textField.prefHeightProperty().bind(controller.getHeightProperty(row));
        textField.minHeightProperty().bind(controller.getHeightProperty(row));
        textField.setAlignment(Pos.CENTER_LEFT);
        textField.setEditable(false);
        textField.setBorder(null);
        textField.setUserData(controller.getCell(row, col));
        textField.setOnKeyPressed(this::onCellKeyPressed);
        textField.setOnMousePressed(this::onCellMousePressed);
        textField.setId(controller.getId(row, col));
        return textField;
    }

    Line createHLine(int row, int col) {
        DoubleProperty widthProperty = controller.getWidthProperty(col);
        Line line = new Line(0, 0, widthProperty.add(1.0).get(), 0);
        line.setStroke(Color.LIGHTGRAY);
        line.endXProperty().bind(widthProperty.add(1.0));
        return line;
    }

    Line createVLine(int row, int col) {
        DoubleProperty heightProperty = controller.getHeightProperty(row);
        Line line = new Line(0, 0, 0, heightProperty.add(1.0).get());
        line.setStroke(Color.LIGHTGRAY);
        line.endYProperty().bind(heightProperty.add(1.0));
        return line;
    }

}
