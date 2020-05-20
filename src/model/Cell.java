package model;

import form.validator.Validators;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Cell {

    private final int row;
    private final int column;
    private StringProperty value = new SimpleStringProperty("");
    private ObjectProperty<CellType> type = new SimpleObjectProperty<>();

    public Cell(int row, int column, String value, CellType type) {
        this.row = row;
        this.column = column;
        this.value.setValue(value);
        this.type.setValue(type);
    }

    public Cell(int row, int column) {
        this(row, column, "", CellType.TEXT);
    }

    public boolean isChanged(StringProperty valueProperty, ObjectProperty<CellType> typeProperty) {
        return !(valueProperty.getValue().equals(this.valueProperty().getValue())
                && typeProperty.getValue().equals(this.typeProperty().getValue()));
    }

    public boolean isValid(StringProperty valueProperty, ObjectProperty<CellType> typeProperty) {
        if (typeProperty.getValue() == CellType.TEXT)
            return true;

        return Validators.NAME.validate(valueProperty.getValue());
    }

    public boolean save(String value, CellType type) {
        if (setValue(value, type)) {
            this.type.setValue(type);
            return true;
        } else {
            return false;
        }
    }

    public int getRow() {
        return row;
    }
    public int getColumn() {
        return column;
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

}
