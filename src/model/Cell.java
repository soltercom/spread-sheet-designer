package model;

import form.validator.Validators;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;

public class Cell {

    private final int row;
    private final int col;
    private final StringProperty value = new SimpleStringProperty("");
    private final ObjectProperty<CellType> type = new SimpleObjectProperty<>();
    private final ObjectProperty<CellBorders> borders = new SimpleObjectProperty<>(new CellBorders());
    private final ObjectProperty<Pos> pos = new SimpleObjectProperty<>(Pos.CENTER_LEFT);

    public Cell(int row, int col, String value, CellType type, CellBorders borders, Pos pos) {
        this.row = row;
        this.col = col;
        this.value.setValue(value);
        this.type.setValue(type);
        this.borders.setValue(borders);
        this.pos.setValue(pos);
    }

    public Cell(int row, int column) {
        this(row, column, "", CellType.TEXT, new CellBorders(), Pos.CENTER_LEFT);
    }

    public boolean isChanged(StringProperty valueProperty, ObjectProperty<CellType> typeProperty) {
        return !(valueProperty.getValue().equals(this.valueProperty().getValue())
                && typeProperty.getValue().equals(this.typeProperty().getValue()));
    }

    public boolean save(String value, CellType type, CellBorders borders, Pos pos) {
        if (setValue(value, type)) {
            this.type.setValue(type);
            this.borders.getValue().setProperties(borders);
            this.pos.setValue(pos);
            return true;
        } else {
            return false;
        }
    }

    public int getRow() {
        return row;
    }
    public int getColumn() {
        return col;
    }
    public String getValue() {
        if (type.getValue() == CellType.TEXT)
            return value.getValue();
        else if (type.getValue() == CellType.PARAMETER)
            return "<" + value.getValue() + ">";
        else
            return "";
    }
    public StringProperty valueProperty() {
        return value;
    }
    public ObjectProperty<CellType> typeProperty() {
        return type;
    }
    public ObjectProperty<CellBorders> bordersProperty() { return borders; }
    public ObjectProperty<Pos> posProperty() { return pos; }
    public boolean setValue(String value, CellType type) {
        if (type == CellType.TEXT) {
            this.value.setValue(value);
            return true;
        } else if (type == CellType.PARAMETER) {
            if (Validators.NAME.validate(value)) {
                this.value.setValue(value);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    public String getId() {
        return "R" + row + "C" + col;
    }
}
