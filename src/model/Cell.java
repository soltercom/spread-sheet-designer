package model;

import form.validator.Validators;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Cell {

    private final int row;
    private final int column;
    private StringProperty value = new SimpleStringProperty("");
    private CellType type;

    public Cell(int row, int column, String value, CellType type) {
        this.row = row;
        this.column = column;
        this.value.setValue(value);
        this.type = type;
    }

    public Cell(int row, int column) {
        this(row, column, "", CellType.TEXT);
    }

    public int getRow() {
        return row;
    }
    public int getColumn() {
        return column;
    }
    public StringProperty valueProperty() {
        return value;
    }
    public boolean setValue(String value) {
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
