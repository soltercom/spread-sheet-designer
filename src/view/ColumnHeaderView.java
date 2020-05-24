package view;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import model.ColumnHeader;

public class ColumnHeaderView extends GridPane {

    private final ColumnHeader model;
    private final SpreadSheetView parentView;

    public ColumnHeaderView(ColumnHeader model, SpreadSheetView parentView) {
        this.model = model;
        this.parentView = parentView;
        init();
        setBindings();
    }

    private void init() {
        addLineConstraints();
        for (int i = 0; i < model.size(); i++) {
            addHeaderConstraints(i);
            addLineConstraints();
        }
        getRowConstraints().add(new RowConstraints(parentView.COLUMN_HEADER_HEIGHT));

        for (int i = 0; i < model.size(); i++) {
            add(createHeader(i),2*i+1, 0, 1, 1);
            add(createLine(i), 2*i+2, 0, 1, 1);
        }

        redraw(0);
    }

    private void setBindings() {
        parentView.hScrollBarMaxProperty().bind(model.calculateWidthProperty().subtract(parentView.spreadSheetWidth));
        parentView.hScrollBarValueProperty().addListener((o, v1, v2) -> redraw(v2.doubleValue()));
        parentView.spreadSheetWidth.addListener(inv -> redraw(parentView.hScrollBarValueProperty().get()));
    }

    private void onLineDragged(MouseEvent event) {
        setCursor(Cursor.H_RESIZE);
        Line line = (Line)event.getSource();
        double dW = event.getX() - line.getStartX();
        int index = (int)line.getUserData();
        model.addColumnWidth(index, dW);
    }

    public void redraw(double dx) {
        setClip(new Rectangle(dx, 0, parentView.spreadSheetWidth.get(), parentView.COLUMN_HEADER_HEIGHT));
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
        getColumnConstraints().add(new ColumnConstraints(parentView.BORDER_WIDTH));
    }

    private TextField createHeader(int index) {
        var textField = new TextField(String.valueOf(index+1));
        textField.setAlignment(Pos.CENTER);
        textField.setEditable(false);
        textField.setBorder(null);
        textField.setUserData(index);
        return textField;
    }

    private Line createLine(int index) {
        Line line = new Line(0, 0, 0, parentView.COLUMN_HEADER_HEIGHT);
        line.setStroke(Color.WHITESMOKE);
        line.setOnMouseEntered(e -> setCursor(Cursor.H_RESIZE));
        line.setOnMouseExited(e -> setCursor(Cursor.DEFAULT));
        line.setUserData(index);
        line.setOnMouseDragged(this::onLineDragged);
        return line;
    }

}
