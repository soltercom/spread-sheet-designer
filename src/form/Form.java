package form;

import form.validator.Validators;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Cell;
import model.CellType;
import view.SpreadSheetViewFixed;

public class Form extends VBox {

    private static final PseudoClass CHANGED_CLASS = PseudoClass.getPseudoClass("changed");
    private static final PseudoClass INVALID_CLASS = PseudoClass.getPseudoClass("invalid");

    private TextField valueField;
    private ComboBox<CellType> typeField;

    private final Button buttonOK = new Button("OK");
    private final Button buttonCancel = new Button("Отмена");

    private final BooleanProperty changed = new SimpleBooleanProperty(false);
    private final BooleanProperty focused = new SimpleBooleanProperty(false);
    private final BooleanProperty valid = new SimpleBooleanProperty(true);

    private final StringProperty valueProperty = new SimpleStringProperty();
    private final BooleanProperty valuePropertyChanged = new SimpleBooleanProperty(false);
    private final BooleanProperty valuePropertyValid = new SimpleBooleanProperty(true);
    private final ObjectProperty<CellType> typeProperty = new SimpleObjectProperty<>();
    private final BooleanProperty typePropertyChanged = new SimpleBooleanProperty(false);

    private Cell cell;

    private final SpreadSheetViewFixed view;

    public Form(SpreadSheetViewFixed view) {
        this.view = view;
        init();
        setBindings();
    }

    private void init() {
        setSpacing(5.0);

        typeField = new ComboBox<>();
        typeField.getItems().addAll(CellType.values());
        typeField.getStyleClass().add("simple-combobox");
        Label typeLabel = new Label("Тип");
        typeLabel.setLabelFor(typeField);
        VBox.setMargin(typeField, new Insets(0, 0, 5.0, 0));
        getChildren().addAll(typeLabel, typeField);

        valueField = new TextField();
        valueField.getStyleClass().add("simple-control");
        Label valueLabel = new Label("Значение");
        valueLabel.setLabelFor(valueField);
        VBox.setMargin(valueField, new Insets(0, 0, 10.0, 0));
        getChildren().addAll(valueLabel, valueField);

        getChildren().add(new HBox(5.0, buttonOK, buttonCancel));
    }

    private void setBindings() {
        valueProperty.bindBidirectional(valueField.textProperty());
        typeProperty.bindBidirectional(typeField.valueProperty());

        buttonOK.disableProperty().bind(changed.not().or(valid.not()));
        buttonOK.setOnAction(e -> this.save());
        buttonCancel.disableProperty().bind(changed.not());
        buttonCancel.setOnAction(e -> this.cancel());

        focused.bind(Bindings.or(valueField.focusedProperty(), typeField.focusedProperty()));
        focused.addListener((o, oldValue, newValue) -> {
            if (newValue) view.beforeEditCell();
            else view.afterEditCell();
        });

    }

    public void setCell(Cell cell) {
        this.cell = cell;
        valueProperty.setValue(cell.valueProperty().getValue());
        typeProperty.setValue(cell.typeProperty().getValue());

        valuePropertyChanged.bind(valueProperty.isNotEqualTo(cell.valueProperty()));
        typePropertyChanged.bind(typeProperty.isNotEqualTo(cell.typeProperty()));
        changed.bind(valuePropertyChanged.or(typePropertyChanged));

        valuePropertyValid.bind(Bindings.createBooleanBinding(() -> {
            if (typeProperty.getValue() == CellType.TEXT) return true;
            return Validators.NAME.validate(valueProperty.getValue());
        }, valueProperty, typeProperty));

        valid.bind(valuePropertyValid);

        valuePropertyChanged.addListener(inv -> valueField.pseudoClassStateChanged(CHANGED_CLASS, valuePropertyChanged.getValue()));
        typePropertyChanged.addListener(inv -> typeField.pseudoClassStateChanged(CHANGED_CLASS, typePropertyChanged.getValue()));
        valuePropertyValid.addListener(inv -> valueField.pseudoClassStateChanged(INVALID_CLASS, !valuePropertyValid.getValue()));
    }

    public void save() {
        if (cell.save(valueProperty.getValue(), typeProperty.getValue())) {
            view.afterEditCell();
        }
    }

    public void cancel() {
        valueProperty.setValue(cell.valueProperty().getValue());
        typeProperty.setValue(cell.typeProperty().getValue());
        view.afterEditCell();
    }

    @Override
    public String getUserAgentStylesheet() {
        return Form.class.getResource("style.css").toExternalForm();
    }

}
