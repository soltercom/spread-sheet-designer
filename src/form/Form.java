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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import model.Cell;
import model.CellBorders;
import model.CellType;
import view.CellBordersView;
import view.SpreadSheetView;

public class Form extends VBox {

    private static final PseudoClass CHANGED_CLASS = PseudoClass.getPseudoClass("changed");
    private static final PseudoClass INVALID_CLASS = PseudoClass.getPseudoClass("invalid");

    private TextField valueField;
    private ComboBox<CellType> typeField;
    private Button editBordersButton;
    private Line topLine;
    private Line rightLine;
    private Line bottomLine;
    private Line leftLine;

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
    private final ObjectProperty<CellBorders> bordersProperty = new SimpleObjectProperty<>(new CellBorders());
    private final BooleanProperty bordersPropertyChanged = new SimpleBooleanProperty(false);

    private Cell cell;

    private final SpreadSheetView parentView;
    private final CellBordersView cellBordersView;

    public Form(SpreadSheetView parentView) {
        this.parentView = parentView;
        this.cellBordersView = new CellBordersView();
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
        VBox.setMargin(valueField, new Insets(0, 5.0, 10.0, 0));
        getChildren().addAll(valueLabel, valueField);

        GridPane bordersPreview = bordersPreview();
        VBox.setMargin(bordersPreview, new Insets(0, 0, 20.0, 0));
        getChildren().add(bordersPreview);

        getChildren().add(new HBox(5.0, buttonOK, buttonCancel));
    }

    private void setBindings() {
        valueProperty.bindBidirectional(valueField.textProperty());
        typeProperty.bindBidirectional(typeField.valueProperty());

        buttonOK.disableProperty().bind(changed.not().or(valid.not()));
        buttonOK.setOnAction(e -> this.save());
        buttonCancel.disableProperty().bind(changed.not());
        buttonCancel.setOnAction(e -> this.cancel());

        focused.bind(Bindings.or(valueField.focusedProperty(), typeField.focusedProperty()).or(editBordersButton.focusedProperty()));
        focused.addListener((o, oldValue, newValue) -> {
            if (newValue) parentView.beforeEditCell();
            else parentView.afterEditCell();
        });

        CellBorders borders = bordersProperty.getValue();
        topLine.strokeProperty().bind(borders.topColorProperty());
        rightLine.strokeProperty().bind(borders.rightColorProperty());
        bottomLine.strokeProperty().bind(borders.bottomColorProperty());
        leftLine.strokeProperty().bind(borders.leftColorProperty());
        topLine.visibleProperty().bind(borders.topBorderProperty());
        rightLine.visibleProperty().bind(borders.rightBorderProperty());
        bottomLine.visibleProperty().bind(borders.bottomBorderProperty());
        leftLine.visibleProperty().bind(borders.leftBorderProperty());
        topLine.opacityProperty().bind(borders.topOpacityProperty());
        rightLine.opacityProperty().bind(borders.rightOpacityProperty());
        bottomLine.opacityProperty().bind(borders.bottomOpacityProperty());
        leftLine.opacityProperty().bind(borders.leftOpacityProperty());
    }

    public void setCell(Cell cell) {
        this.cell = cell;

        valueProperty.set(cell.valueProperty().getValue());
        typeProperty.setValue(cell.typeProperty().getValue());
        bordersProperty.getValue().setProperties(cell.bordersProperty().getValue());

        valuePropertyChanged.bind(valueProperty.isNotEqualTo(cell.valueProperty()));
        typePropertyChanged.bind(typeProperty.isNotEqualTo(cell.typeProperty()));
        bordersPropertyChanged.bind(bordersProperty.getValue().isChanged(cell.bordersProperty()));
        changed.bind(valuePropertyChanged.or(typePropertyChanged).or(bordersPropertyChanged));

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
        if (cell.save(valueProperty.getValue(), typeProperty.getValue(), bordersProperty.getValue())) {
            parentView.afterEditCell();
        }
    }

    public void cancel() {
        valueProperty.setValue(cell.valueProperty().getValue());
        typeProperty.setValue(cell.typeProperty().getValue());
        bordersProperty.getValue().setProperties(cell.bordersProperty().getValue());
        parentView.afterEditCell();
    }

    public GridPane bordersPreview() {

        double WIDTH = 200.0D;
        double HEIGHT = 25.0D;

        GridPane pane = new GridPane();

        pane.getColumnConstraints().add(new ColumnConstraints(1.0D));
        pane.getColumnConstraints().add(new ColumnConstraints(3.0D));
        pane.getColumnConstraints().add(new ColumnConstraints(WIDTH));
        pane.getColumnConstraints().add(new ColumnConstraints(3.0D));
        pane.getColumnConstraints().add(new ColumnConstraints(1.0D));

        pane.getRowConstraints().add(new RowConstraints(1.0D));
        pane.getRowConstraints().add(new RowConstraints(3.0D));
        pane.getRowConstraints().add(new RowConstraints(HEIGHT));
        pane.getRowConstraints().add(new RowConstraints(3.0D));
        pane.getRowConstraints().add(new RowConstraints(1.0D));

        topLine = new Line(0, 0, WIDTH, 0);
        rightLine = new Line(0, 0, 0, HEIGHT);
        bottomLine = new Line(0, 0, WIDTH, 0);
        leftLine = new Line(0, 0, 0, HEIGHT);
        pane.add(topLine, 2, 0, 1, 1);
        pane.add(leftLine, 0, 2, 1, 1);
        pane.add(rightLine, 4, 2, 1, 1);
        pane.add(bottomLine, 2, 4, 1, 1);

        editBordersButton = new Button("Редакировать границы");
        editBordersButton.setPrefSize(WIDTH, HEIGHT);
        pane.add(editBordersButton, 2, 2, 1, 1);
        editBordersButton.setOnAction(e -> {
            cellBordersView.edit(bordersProperty.getValue())
                    .ifPresent(result -> bordersProperty.getValue().setProperties(result));
            editBordersButton.requestFocus();
        });

        return pane;
    }


    @Override
    public String getUserAgentStylesheet() {
        return Form.class.getResource("style.css").toExternalForm();
    }

}
