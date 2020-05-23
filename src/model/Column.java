package model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Column {

    private final static double DEFAULT_WIDTH = 75.0D;
    private final static double MIN_WIDTH = 25.0D;

    private final int num;
    private final DoubleProperty width = new SimpleDoubleProperty(DEFAULT_WIDTH);

    public Column(int num, double width) {
        this.num = num;
        this.width.set(width);
    }

    public Column(int num) {
        this(num, DEFAULT_WIDTH);
    }

    public int getNum() {
        return num;
    }
    public DoubleProperty widthProperty() { return width; }
    public double getWidth() { return width.get(); }
    public void addWidth(double w) {
        double newWidth = getWidth() + w;
        if (newWidth >= MIN_WIDTH)
            width.set(newWidth);
    }
}
