package model;

import form.validator.Validators;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.List;

public class ColumnHeader extends Header {

    private final double BORDER_WIDTH = 1.0D;

    private final List<Column> list = new ArrayList<>();
    private DoubleBinding calculateWidth;

    private final DoubleProperty width = new SimpleDoubleProperty(0.0D);

    public ColumnHeader() {
        super();
        calculateWidth = width.add(BORDER_WIDTH);
    }

    public void add(Column column) {
        list.add(column);
        calculateWidth = calculateWidth.add(column.widthProperty()).add(BORDER_WIDTH);
    }

    public DoubleProperty getLengthProperty(int index) {
        return list.get(index).widthProperty();
    }

    public int size() { return list.size(); }

    public void addLength(int index, double dW) { list.get(index).addWidth(dW); }

    public DoubleBinding calculateLengthProperty() {
        return calculateWidth;
    }

    public DoubleProperty getBorderLengthProperty() { return new SimpleDoubleProperty(BORDER_WIDTH); }
}
