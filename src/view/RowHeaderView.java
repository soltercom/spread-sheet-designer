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
import model.RowHeader;
import org.jetbrains.annotations.NotNull;

public class RowHeaderView extends GridPane {

    private final RowHeader model;
    private final SpreadSheetView parentView;

    public RowHeaderView(RowHeader model, SpreadSheetView parentView) {
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
        getColumnConstraints().add(new ColumnConstraints(parentView.ROW_HEADER_WIDTH));

        for (int i = 0; i < model.size(); i++) {
            add(createHeader(i),0, 2*i+1, 1, 1);
            add(createLine(i), 0, 2*i+2, 1, 1);
        }

        redraw(0);
    }

    private void setBindings() {
        parentView.vScrollBarMaxProperty().bind(model.calculateHeightProperty().subtract(parentView.spreadSheetHeight));
        parentView.vScrollBarValueProperty().addListener((o, v1, v2) -> redraw(v2.doubleValue()));
        parentView.spreadSheetHeight.addListener(inv -> redraw(parentView.vScrollBarValueProperty().get()));
    }

    private void onLineDragged(MouseEvent event) {
        setCursor(Cursor.V_RESIZE);
        Line line = (Line)event.getSource();
        double dW = event.getY() - line.getStartY();
        int index = (int)line.getUserData();
        model.addRowHeight(index, dW);
    }

    public void redraw(double dy) {
        setClip(new Rectangle(0, dy, parentView.ROW_HEADER_WIDTH, parentView.spreadSheetHeight.get()));
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
        getRowConstraints().add(new RowConstraints(parentView.BORDER_WIDTH));
    }

    private TextField createHeader(int index) {
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

    private Line createLine(int index) {
        Line line = new Line(0, 0, parentView.ROW_HEADER_WIDTH, 0);
        line.setStroke(Color.WHITESMOKE);
        line.setOnMouseEntered(e -> setCursor(Cursor.V_RESIZE));
        line.setOnMouseExited(e -> setCursor(Cursor.DEFAULT));
        line.setUserData(index);
        line.setOnMouseDragged(this::onLineDragged);
        return line;
    }

}
