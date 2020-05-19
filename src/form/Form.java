package form;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Cell;
import view.SpreadSheetView;

public class Form extends VBox {

    private static final PseudoClass CHANGED_CLASS = PseudoClass.getPseudoClass("changed");

    private TextField valueField;
    private final Button buttonOK = new Button("OK");
    private final Button buttonCancel = new Button("Отмена");

    private BooleanProperty changed = new SimpleBooleanProperty(false);
    private BooleanProperty focused = new SimpleBooleanProperty(false);
    private StringProperty valueProperty = new SimpleStringProperty();

    private Cell cell;

    private SpreadSheetView view;

    public Form(SpreadSheetView view) {
        this.view = view;
        init();
        setBindings();
        this.focusedProperty().addListener(o ->System.out.println("FOCUSED"));
    }

    private void init() {
        setSpacing(5.0);

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

        buttonOK.disableProperty().bind(changed.not());
        buttonOK.setOnAction(e -> this.save());
        buttonCancel.disableProperty().bind(changed.not());
        buttonCancel.setOnAction(e -> this.cancel());

        focused.bind(Bindings.createBooleanBinding(() -> valueField.focusedProperty().getValue(),
                valueField.focusedProperty()));
        focused.addListener((o, oldValue, newValue) -> {
            if (newValue) view.beforeEditCell();
            else view.afterEditCell();
        });

        changed.addListener(inv -> valueField.pseudoClassStateChanged(CHANGED_CLASS, changed.getValue()));
    }

    public void setCell(Cell cell) {
        this.cell = cell;
        valueProperty.setValue(cell.valueProperty().getValue());
        changed.unbind();
        changed.bind(Bindings.createBooleanBinding(() -> !valueProperty.getValue().equals(cell.valueProperty().getValue()), valueProperty, cell.valueProperty()));
    }

    public void save() {
        if (cell.setValue(valueProperty.getValue())) {
            view.afterEditCell();
        }
    }

    public void cancel() {
        valueProperty.setValue(cell.valueProperty().getValue());
        view.afterEditCell();
    }

    @Override
    public String getUserAgentStylesheet() {
        return Form.class.getResource("style.css").toExternalForm();
    }

}
