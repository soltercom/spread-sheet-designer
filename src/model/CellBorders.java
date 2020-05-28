package model;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.scene.paint.Color;

public class CellBorders {

    private static class CellBorder {

        private final BooleanProperty border = new SimpleBooleanProperty(false);
        private final IntegerProperty opacity = new SimpleIntegerProperty(0);
        private final ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.BLACK);

        public CellBorder() {}

        public BooleanProperty borderProperty() { return border; }
        public ObjectProperty<Color> colorProperty() { return color; }
        public IntegerProperty opacityProperty() { return opacity; }

        public BooleanBinding isChanged(CellBorder border) {
            return borderProperty().isNotEqualTo(border.borderProperty())
            .or(colorProperty().isNotEqualTo(border.colorProperty()))
            .or(opacityProperty().isNotEqualTo(border.opacityProperty()));
        }
    }

    private final ObjectProperty<CellBorder> top, right, bottom, left;

    public CellBorders() {
        top = new SimpleObjectProperty<>(new CellBorder());
        right = new SimpleObjectProperty<>(new CellBorder());
        bottom = new SimpleObjectProperty<>(new CellBorder());
        left = new SimpleObjectProperty<>(new CellBorder());
    }

    public void setProperties(CellBorders borders) {
        topBorderProperty().setValue(borders.topBorderProperty().getValue());
        rightBorderProperty().setValue(borders.rightBorderProperty().getValue());
        bottomBorderProperty().setValue(borders.bottomBorderProperty().getValue());
        leftBorderProperty().setValue(borders.leftBorderProperty().getValue());
        topColorProperty().setValue(borders.topColorProperty().getValue());
        rightColorProperty().setValue(borders.rightColorProperty().getValue());
        bottomColorProperty().setValue(borders.bottomColorProperty().getValue());
        leftColorProperty().setValue(borders.leftColorProperty().getValue());
        topOpacityProperty().setValue(borders.topOpacityProperty().getValue());
        rightOpacityProperty().setValue(borders.rightOpacityProperty().getValue());
        bottomOpacityProperty().setValue(borders.bottomOpacityProperty().getValue());
        leftOpacityProperty().setValue(borders.leftOpacityProperty().getValue());
    }

    public BooleanBinding isChanged(ObjectProperty<CellBorders> bordersProperty) {
        CellBorders borders = bordersProperty.getValue();
        return top.getValue().isChanged(borders.top.getValue())
        .or(right.getValue().isChanged(borders.right.getValue()))
        .or(bottom.getValue().isChanged(borders.bottom.getValue()))
        .or(left.getValue().isChanged(borders.left.getValue()));
    }

    public BooleanProperty topBorderProperty() { return top.getValue().borderProperty(); }
    public BooleanProperty rightBorderProperty() { return right.getValue().borderProperty(); }
    public BooleanProperty bottomBorderProperty() { return bottom.getValue().borderProperty(); }
    public BooleanProperty leftBorderProperty() { return left.getValue().borderProperty(); }
    public ObjectProperty<Color> topColorProperty() { return top.getValue().colorProperty(); }
    public ObjectProperty<Color> rightColorProperty() { return top.getValue().colorProperty(); }
    public ObjectProperty<Color> bottomColorProperty() { return top.getValue().colorProperty(); }
    public ObjectProperty<Color> leftColorProperty() { return top.getValue().colorProperty(); }
    public IntegerProperty topOpacityProperty() { return top.getValue().opacityProperty(); }
    public IntegerProperty rightOpacityProperty() { return right.getValue().opacityProperty(); }
    public IntegerProperty bottomOpacityProperty() { return bottom.getValue().opacityProperty(); }
    public IntegerProperty leftOpacityProperty() { return left.getValue().opacityProperty(); }

}
