package form;

import form.validator.Validators;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private final ToggleGroup hAlignmentGroup = new ToggleGroup();
    private final ToggleGroup vAlignmentGroup = new ToggleGroup();
    private ToggleButton hAlignmentLeft, hAlignmentCenter, hAlignmentRight,
            vAlignmentTop, vAlignmentCenter, vAlignmentBottom;

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
    private final ObjectProperty<Pos> posProperty = new SimpleObjectProperty<>();
    private final BooleanProperty posPropertyChanged = new SimpleBooleanProperty(false);

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

        GridPane alignmentView = alignmentView();
        getChildren().add(alignmentView);

        getChildren().add(new HBox(5.0, buttonOK, buttonCancel));
    }

    private void setBindings() {
        valueProperty.bindBidirectional(valueField.textProperty());
        typeProperty.bindBidirectional(typeField.valueProperty());

        buttonOK.disableProperty().bind(changed.not().or(valid.not()));
        buttonOK.setOnAction(e -> this.save());
        buttonCancel.disableProperty().bind(changed.not());
        buttonCancel.setOnAction(e -> this.cancel());

        focused.bind(Bindings.or(valueField.focusedProperty(), typeField.focusedProperty()).or(editBordersButton.focusedProperty()).or(posPropertyChanged));
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

        Platform.runLater(() -> {
            hAlignmentLeft.disableProperty()
                    .bind(Bindings.createBooleanBinding(() -> posProperty.getValue().getHpos().equals(HPos.LEFT), posProperty));
            hAlignmentCenter.disableProperty()
                    .bind(Bindings.createBooleanBinding(() -> posProperty.getValue().getHpos().equals(HPos.CENTER), posProperty));
            hAlignmentRight.disableProperty()
                    .bind(Bindings.createBooleanBinding(() -> posProperty.getValue().getHpos().equals(HPos.RIGHT), posProperty));
            vAlignmentTop.disableProperty()
                    .bind(Bindings.createBooleanBinding(() -> posProperty.getValue().getVpos().equals(VPos.TOP), posProperty));
            vAlignmentCenter.disableProperty()
                    .bind(Bindings.createBooleanBinding(() -> posProperty.getValue().getVpos().equals(VPos.CENTER), posProperty));
            vAlignmentBottom.disableProperty()
                    .bind(Bindings.createBooleanBinding(() -> posProperty.getValue().getVpos().equals(VPos.BOTTOM), posProperty));
        });
    }

    public void setCell(Cell cell) {
        this.cell = cell;

        valueProperty.set(cell.valueProperty().getValue());
        typeProperty.setValue(cell.typeProperty().getValue());
        bordersProperty.getValue().setProperties(cell.bordersProperty().getValue());
        posProperty.setValue(cell.posProperty().getValue());

        valuePropertyChanged.bind(valueProperty.isNotEqualTo(cell.valueProperty()));
        typePropertyChanged.bind(typeProperty.isNotEqualTo(cell.typeProperty()));
        bordersPropertyChanged.bind(bordersProperty.getValue().isChanged(cell.bordersProperty()));
        posPropertyChanged.bind(posProperty.isNotEqualTo(cell.posProperty()));
        changed.bind(valuePropertyChanged.or(typePropertyChanged).or(bordersPropertyChanged).or(posPropertyChanged));

        valuePropertyValid.bind(Bindings.createBooleanBinding(() -> {
            if (typeProperty.getValue() == CellType.TEXT) return true;
            return Validators.NAME.validate(valueProperty.getValue());
        }, valueProperty, typeProperty));

        valid.bind(valuePropertyValid);

        valuePropertyChanged.addListener(inv -> valueField.pseudoClassStateChanged(CHANGED_CLASS, valuePropertyChanged.getValue()));
        typePropertyChanged.addListener(inv -> typeField.pseudoClassStateChanged(CHANGED_CLASS, typePropertyChanged.getValue()));
        valuePropertyValid.addListener(inv -> valueField.pseudoClassStateChanged(INVALID_CLASS, !valuePropertyValid.getValue()));

        setAlignmentButtons();
    }

    public void save() {
        if (cell.save(valueProperty.getValue(), typeProperty.getValue(), bordersProperty.getValue(), posProperty.getValue())) {
            parentView.afterEditCell();
        }
    }

    public void cancel() {
        valueProperty.setValue(cell.valueProperty().getValue());
        typeProperty.setValue(cell.typeProperty().getValue());
        bordersProperty.getValue().setProperties(cell.bordersProperty().getValue());
        posProperty.setValue(cell.posProperty().getValue());
        setAlignmentButtons();
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

        editBordersButton = new Button("Редактировать границы");
        editBordersButton.setPrefSize(WIDTH, HEIGHT);
        pane.add(editBordersButton, 2, 2, 1, 1);
        editBordersButton.setOnAction(e -> {
            cellBordersView.edit(bordersProperty.getValue())
                    .ifPresent(result -> bordersProperty.getValue().setProperties(result));
            editBordersButton.requestFocus();
        });

        return pane;
    }

    private void setPosProperty(ActionEvent event) {
        ToggleButton hButton = (ToggleButton) hAlignmentGroup.getSelectedToggle();
        ToggleButton vButton = (ToggleButton) vAlignmentGroup.getSelectedToggle();

        if (hButton == null || vButton == null) { return; }

        HPos hPos = (HPos) hButton.getUserData();
        VPos vPos = (VPos) vButton.getUserData();

        if ((hPos == HPos.LEFT) && (vPos == VPos.TOP))
            posProperty.setValue(Pos.TOP_LEFT);
        else if ((hPos == HPos.CENTER) && (vPos == VPos.TOP))
            posProperty.setValue(Pos.TOP_CENTER);
        else if ((hPos == HPos.RIGHT) && (vPos == VPos.TOP))
            posProperty.setValue(Pos.TOP_RIGHT);
        else if ((hPos == HPos.LEFT) && (vPos == VPos.CENTER))
            posProperty.setValue(Pos.CENTER_LEFT);
        else if ((hPos == HPos.CENTER) && (vPos == VPos.CENTER))
            posProperty.setValue(Pos.CENTER);
        else if ((hPos == HPos.RIGHT) && (vPos == VPos.CENTER))
            posProperty.setValue(Pos.CENTER_RIGHT);
        else if ((hPos == HPos.LEFT) && (vPos == VPos.BOTTOM))
            posProperty.setValue(Pos.BOTTOM_LEFT);
        else if ((hPos == HPos.CENTER) && (vPos == VPos.BOTTOM))
            posProperty.setValue(Pos.BOTTOM_CENTER);
        else if ((hPos == HPos.RIGHT) && (vPos == VPos.BOTTOM))
            posProperty.setValue(Pos.BOTTOM_RIGHT);
    }

    private void setAlignmentButtons() {
        HPos hPos = posProperty.getValue().getHpos();
        VPos vPos = posProperty.getValue().getVpos();

        if (hPos.equals(HPos.LEFT))
            hAlignmentGroup.selectToggle(hAlignmentLeft);
        else if (hPos.equals(HPos.CENTER))
            hAlignmentGroup.selectToggle(hAlignmentCenter);
        else if (hPos.equals(HPos.RIGHT))
            hAlignmentGroup.selectToggle(hAlignmentRight);
        if (vPos.equals(VPos.TOP))
            vAlignmentGroup.selectToggle(vAlignmentTop);
        else if (vPos.equals(VPos.CENTER))
            vAlignmentGroup.selectToggle(vAlignmentCenter);
        else if (vPos.equals(VPos.BOTTOM))
            vAlignmentGroup.selectToggle(vAlignmentBottom);
    }

    private GridPane alignmentView() {

        GridPane alignmentView = new GridPane();

        alignmentView.getColumnConstraints().add(new ColumnConstraints(54.0D));
        alignmentView.getColumnConstraints().add(new ColumnConstraints(54.0D));
        alignmentView.getColumnConstraints().add(new ColumnConstraints(54.0D));

        alignmentView.getRowConstraints().add(new RowConstraints(54.0D));
        alignmentView.getRowConstraints().add(new RowConstraints(54.0D));
        alignmentView.getRowConstraints().add(new RowConstraints(54.0D));

        ImageView VERTICAL_ALIGN_TOP
            = new ImageView(new Image(CellBordersView.class.getResourceAsStream("vertical_align_top.png")));
        ImageView VERTICAL_ALIGN_CENTER
                = new ImageView(new Image(CellBordersView.class.getResourceAsStream("vertical_align_center.png")));
        ImageView VERTICAL_ALIGN_BOTTOM
                = new ImageView(new Image(CellBordersView.class.getResourceAsStream("vertical_align_bottom.png")));
        ImageView HORIZONTAL_ALIGN_LEFT
                = new ImageView(new Image(CellBordersView.class.getResourceAsStream("horizontal_align_left.png")));
        ImageView HORIZONTAL_ALIGN_CENTER
                = new ImageView(new Image(CellBordersView.class.getResourceAsStream("horizontal_align_center.png")));
        ImageView HORIZONTAL_ALIGN_RIGHT
                = new ImageView(new Image(CellBordersView.class.getResourceAsStream("horizontal_align_right.png")));

        hAlignmentLeft = new ToggleButton("", HORIZONTAL_ALIGN_LEFT);
        hAlignmentCenter = new ToggleButton("", HORIZONTAL_ALIGN_CENTER);
        hAlignmentRight = new ToggleButton("", HORIZONTAL_ALIGN_RIGHT);
        hAlignmentLeft.setToggleGroup(hAlignmentGroup);
        hAlignmentCenter.setToggleGroup(hAlignmentGroup);
        hAlignmentRight.setToggleGroup(hAlignmentGroup);
        alignmentView.add(hAlignmentLeft, 0, 1, 1, 1);
        alignmentView.add(hAlignmentCenter, 1, 1, 1, 1);
        alignmentView.add(hAlignmentRight, 2, 1, 1, 1);
        hAlignmentLeft.setUserData(HPos.LEFT);
        hAlignmentCenter.setUserData(HPos.CENTER);
        hAlignmentRight.setUserData(HPos.RIGHT);
        hAlignmentLeft.setOnAction(this::setPosProperty);
        hAlignmentCenter.setOnAction(this::setPosProperty);
        hAlignmentRight.setOnAction(this::setPosProperty);

        vAlignmentTop = new ToggleButton("", VERTICAL_ALIGN_TOP);
        vAlignmentCenter = new ToggleButton("", VERTICAL_ALIGN_CENTER);
        vAlignmentBottom = new ToggleButton("", VERTICAL_ALIGN_BOTTOM);
        vAlignmentTop.setToggleGroup(vAlignmentGroup);
        vAlignmentCenter.setToggleGroup(vAlignmentGroup);
        vAlignmentBottom.setToggleGroup(vAlignmentGroup);
        alignmentView.add(vAlignmentTop, 0, 0, 1, 1);
        alignmentView.add(vAlignmentCenter, 1, 0, 1, 1);
        alignmentView.add(vAlignmentBottom, 2, 0, 1, 1);
        vAlignmentTop.setUserData(VPos.TOP);
        vAlignmentCenter.setUserData(VPos.CENTER);
        vAlignmentBottom.setUserData(VPos.BOTTOM);
        vAlignmentTop.setOnAction(this::setPosProperty);
        vAlignmentCenter.setOnAction(this::setPosProperty);
        vAlignmentBottom.setOnAction(this::setPosProperty);

        return alignmentView;
    }

    @Override
    public String getUserAgentStylesheet() {
        return Form.class.getResource("style.css").toExternalForm();
    }

}
