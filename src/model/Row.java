package model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Row {

    private final static double DEFAULT_HEIGHT = 25.0D;
    private final static double MIN_HEIGHT = 5.0D;

    private final int num;
    private final DoubleProperty height = new SimpleDoubleProperty(DEFAULT_HEIGHT);

    public Row(int num, double height) {
        this.num = num;
        this.height.set(height);
    }

    public Row(int num) {
        this(num, DEFAULT_HEIGHT);
    }

    public int getNum() {
        return num;
    }
    public DoubleProperty heightProperty() { return height; }
    public double getHeight() { return height.get(); }
    public void addHeight(double h) {
        double newHeight = getHeight() + h;
        if (newHeight >= MIN_HEIGHT)
            height.set(newHeight);
    }

}
