package view;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import model.RowHeader;
import org.jetbrains.annotations.NotNull;

public class RowHeaderView extends GridPane {

    private static final double ROW_HEADER_WIDTH = 75.0D;
    private static final double BORDER_HEIGHT = 1.0D;
    private static final double ROW_HEADER_HEIGHT = 600.0D;

    private final RowHeader model;
    private final ScrollBar vScrollBar;

    public RowHeaderView(RowHeader model, ScrollBar vScrollBar) {
        this.model = model;
        this.vScrollBar = vScrollBar;
        init();
        setBindings();
    }

    private void init() {
        addLineConstraints();
        for (int i = 0; i < model.size(); i++) {
            addHeaderConstraints(i);
            addLineConstraints();
        }
        getColumnConstraints().add(new ColumnConstraints(ROW_HEADER_WIDTH));

        for (int i = 0; i < model.size(); i++) {
            add(createHeader(i),0, 2*i+1, 1, 1);
            add(createLine(i), 0, 2*i+2, 1, 1);
        }

        redraw(0);
    }

    private void setBindings() {
        vScrollBar.maxProperty().bind(model.calculateHeightProperty().subtract(ROW_HEADER_HEIGHT));
        vScrollBar.valueProperty().addListener((o, v1, v2) -> redraw(v2.doubleValue()));
    }

    private void onLineDragged(@NotNull MouseEvent event) {
        setCursor(Cursor.V_RESIZE);
        Line line = (Line)event.getSource();
        double dW = event.getY() - line.getStartY();
        int index = (int)line.getUserData();
        model.addRowHeight(index, dW);
    }

    public void redraw(double dy) {
        setClip(new Rectangle(0, dy, ROW_HEADER_WIDTH, ROW_HEADER_HEIGHT));
        setTranslateY(-dy);
    }

    private void addHeaderConstraints(int index) {
        DoubleProperty heightProperty = model.get(index).heightProperty();
        RowConstraints constraints = new RowConstraints(heightProperty.get());
        constraints.prefHeightProperty().bind(heightProperty);
        constraints.minHeightProperty().bind(heightProperty);
        constraints.maxHeightProperty().bind(heightProperty);
        getRowConstraints().add(constraints);
    }

    private void addLineConstraints() {
        getRowConstraints().add(new RowConstraints(BORDER_HEIGHT));
    }

    private @NotNull
    TextField createHeader(int index) {
        TextField textField = new TextField(String.valueOf(index+1));
        textField.setAlignment(Pos.CENTER);
        textField.setEditable(false);
        textField.setBorder(null);
        textField.setUserData(index);
        textField.maxHeightProperty().bind(model.get(index).heightProperty());
        textField.prefHeightProperty().bind(model.get(index).heightProperty());
        textField.minHeightProperty().bind(model.get(index).heightProperty());
        return textField;
    }

    private @NotNull Line createLine(int index) {
        Line line = new Line(0, 0, ROW_HEADER_WIDTH, 0);
        line.setStroke(Color.WHITESMOKE);
        line.setOnMouseEntered(e -> setCursor(Cursor.V_RESIZE));
        line.setOnMouseExited(e -> setCursor(Cursor.DEFAULT));
        line.setUserData(index);
        line.setOnMouseDragged(this::onLineDragged);
        return line;
    }

}
