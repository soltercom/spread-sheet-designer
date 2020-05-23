package model;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.ArrayList;
import java.util.List;

public class RowHeader {

    private final double BORDER_HEIGHT = 1.0D;

    private final List<Row> list = new ArrayList<>();
    private DoubleBinding calculateHeight;

    private final DoubleProperty height = new SimpleDoubleProperty(0.0D);

    public RowHeader() {
        calculateHeight = height.add(BORDER_HEIGHT);
    }

    public void add(Row row) {
        list.add(row);
        calculateHeight = calculateHeight.add(row.heightProperty()).add(BORDER_HEIGHT);
    }

    public Row get(int index) {
        return list.get(index);
    }

    public int size() { return list.size(); }

    public DoubleProperty heightProperty() {
        return height;
    }

    public void addRowHeight(int index, double dH) { get(index).addHeight(dH); }

    public DoubleBinding calculateHeightProperty() {
        return calculateHeight;
    }

}
