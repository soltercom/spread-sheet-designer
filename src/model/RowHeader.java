package model;

import form.validator.Validators;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.ArrayList;
import java.util.List;

public class RowHeader extends Header {

    private final double BORDER_HEIGHT = 1.0D;

    private final List<Row> list = new ArrayList<>();
    private DoubleBinding calculateHeight;

    private final DoubleProperty height = new SimpleDoubleProperty(0.0D);

    public RowHeader() {
        super();
        calculateHeight = height.add(BORDER_HEIGHT);
    }

    public void add(Row row) {
        list.add(row);
        calculateHeight = calculateHeight.add(row.heightProperty()).add(BORDER_HEIGHT);
    }

    public DoubleProperty getLengthProperty(int index) {
        return list.get(index).heightProperty();
    }

    public int size() { return list.size(); }

    public void addLength(int index, double dH) { list.get(index).addHeight(dH); }

    public DoubleBinding calculateLengthProperty() {
        return calculateHeight;
    }

    public DoubleProperty getBorderLengthProperty() { return new SimpleDoubleProperty(BORDER_HEIGHT); }
}
