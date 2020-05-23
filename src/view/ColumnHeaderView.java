package view;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import model.ColumnHeader;
import org.jetbrains.annotations.NotNull;

public class ColumnHeaderView extends GridPane {

    private static final double COLUMN_HEADER_HEIGHT = 25.0D;
    private static final double BORDER_WIDTH = 1.0D;
    private static final double COLUMN_HEADER_WIDTH = 1000.0;

    private final ColumnHeader model;
    private final ScrollBar hScrollBar;

    public ColumnHeaderView(ColumnHeader model, ScrollBar hScrollBar) {
        this.model = model;
        this.hScrollBar = hScrollBar;
        init();
        setBindings();
    }

    private void init() {
        addLineConstraints();
        for (int i = 0; i < model.size(); i++) {
            addHeaderConstraints(i);
            addLineConstraints();
        }
        getRowConstraints().add(new RowConstraints(COLUMN_HEADER_HEIGHT));

        for (int i = 0; i < model.size(); i++) {
            add(createHeader(i),2*i+1, 0, 1, 1);
            add(createLine(i), 2*i+2, 0, 1, 1);
        }

        redraw(0);
    }

    private void setBindings() {
        hScrollBar.maxProperty().bind(model.calculateWidthProperty().subtract(COLUMN_HEADER_WIDTH));
        hScrollBar.valueProperty().addListener((o, v1, v2) -> redraw(v2.doubleValue()));
    }

    private void onLineDragged(@NotNull MouseEvent event) {
        setCursor(Cursor.H_RESIZE);
        Line line = (Line)event.getSource();
        double dW = event.getX() - line.getStartX();
        int index = (int)line.getUserData();
        model.addColumnWidth(index, dW);
    }

    public void redraw(double dx) {
        setClip(new Rectangle(dx, 0, COLUMN_HEADER_WIDTH, COLUMN_HEADER_HEIGHT));
        setTranslateX(-dx);
    }

    private void addHeaderConstraints(int index) {
        DoubleProperty widthProperty = model.get(index).widthProperty();
        ColumnConstraints constraints = new ColumnConstraints(widthProperty.get());
        constraints.prefWidthProperty().bind(widthProperty);
        constraints.minWidthProperty().bind(widthProperty);
        constraints.maxWidthProperty().bind(widthProperty);
        getColumnConstraints().add(constraints);
    }

    private void addLineConstraints() {
        getColumnConstraints().add(new ColumnConstraints(BORDER_WIDTH));
    }

    private @NotNull
    TextField createHeader(int index) {
        var textField = new TextField(String.valueOf(index+1));
        textField.setAlignment(Pos.CENTER);
        textField.setEditable(false);
        textField.setBorder(null);
        textField.setUserData(index);
        return textField;
    }

    private @NotNull Line createLine(int index) {
        Line line = new Line(0, 0, 0, COLUMN_HEADER_HEIGHT);
        line.setStroke(Color.WHITESMOKE);
        line.setOnMouseEntered(e -> setCursor(Cursor.H_RESIZE));
        line.setOnMouseExited(e -> setCursor(Cursor.DEFAULT));
        line.setUserData(index);
        line.setOnMouseDragged(this::onLineDragged);
        return line;
    }

}
