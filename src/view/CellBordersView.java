package view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import model.CellBorders;

import java.util.Optional;

public class CellBordersView extends GridPane {

    private static Image getImage(String path) {
        return new Image(CellBordersView.class.getResourceAsStream(path));
    }

    private final static ImageView BORDER_TOP_IMAGE
            = new ImageView(getImage("border_top.png"));
    private final static ImageView BORDER_RIGHT_IMAGE
            = new ImageView(getImage("border_right.png"));
    private final static ImageView BORDER_BOTTOM_IMAGE
            = new ImageView(getImage("border_bottom.png"));
    private final static ImageView BORDER_LEFT_IMAGE
            = new ImageView(getImage("border_left.png"));

    private ToggleButton topButton;
    private ToggleButton rightButton;
    private ToggleButton bottomButton;
    private ToggleButton leftButton;
    private Spinner<Integer> topSpinner;
    private Spinner<Integer> rightSpinner;
    private Spinner<Integer> bottomSpinner;
    private Spinner<Integer> leftSpinner;
    private ColorPicker topColorPicker;
    private ColorPicker rightColorPicker;
    private ColorPicker bottomColorPicker;
    private ColorPicker leftColorPicker;

    public CellBordersView() {
        init();
    }

    public Optional<CellBorders> edit(CellBorders borders) {
        ButtonType buttonOK = new ButtonType("ОК", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);

        setControlValues(borders);

        Dialog<CellBorders> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(this);
        dialog.getDialogPane().getButtonTypes().addAll(buttonOK, buttonCancel);
        dialog.setResultConverter(p -> {
            if (p.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                return getControlValues();
            } else {
                return null;
            }
        });

        return dialog.showAndWait();
    }

    private void init() {

        setHgap(10.0D);
        setVgap(5.0D);

        getColumnConstraints().add(new ColumnConstraints(50.0D));
        getColumnConstraints().add(new ColumnConstraints(100.0D));
        getColumnConstraints().add(new ColumnConstraints(100.0D));

        getRowConstraints().add(new RowConstraints(50.0D));
        getRowConstraints().add(new RowConstraints(50.0D));
        getRowConstraints().add(new RowConstraints(50.0D));
        getRowConstraints().add(new RowConstraints(50.0D));
        getRowConstraints().add(new RowConstraints(50.0D));

        topButton = new ToggleButton("", BORDER_TOP_IMAGE);
        topSpinner = new Spinner<>();
        topColorPicker = new ColorPicker();

        rightButton = new ToggleButton("", BORDER_RIGHT_IMAGE);
        rightSpinner = new Spinner<>();
        rightColorPicker = new ColorPicker();

        bottomButton = new ToggleButton("", BORDER_BOTTOM_IMAGE);
        bottomSpinner = new Spinner<>();
        bottomColorPicker = new ColorPicker();

        leftButton = new ToggleButton("", BORDER_LEFT_IMAGE);
        leftSpinner = new Spinner<>();
        leftColorPicker = new ColorPicker();

        add(topButton, 0,0, 1, 1);
        add(topSpinner, 1,0, 1, 1);
        add(topColorPicker, 2,0, 1, 1);
        add(rightButton, 0,1, 1, 1);
        add(rightSpinner, 1,1, 1, 1);
        add(rightColorPicker, 2,1, 1, 1);
        add(bottomButton, 0,2, 1, 1);
        add(bottomSpinner, 1,2, 1, 1);
        add(bottomColorPicker, 2,2, 1, 1);
        add(leftButton, 0,3, 1, 1);
        add(leftSpinner, 1,3, 1, 1);
        add(leftColorPicker, 2,3, 1, 1);

    }

    private void setControlValues(CellBorders borders) {
        topButton.setSelected(borders.topBorderProperty().get());
        topSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, borders.topOpacityProperty().get(), 25));
        topColorPicker.setValue(borders.topColorProperty().getValue());

        rightButton.setSelected(borders.rightBorderProperty().get());
        rightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, borders.rightOpacityProperty().get(), 25));
        rightColorPicker.setValue(borders.rightColorProperty().getValue());

        bottomButton.setSelected(borders.bottomBorderProperty().get());
        bottomSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, borders.bottomOpacityProperty().get(), 25));
        bottomColorPicker.setValue(borders.bottomColorProperty().getValue());

        leftButton.setSelected(borders.leftBorderProperty().get());
        leftSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, borders.leftOpacityProperty().get(), 25));
        leftColorPicker.setValue(borders.leftColorProperty().getValue());
    }

    private CellBorders getControlValues() {
        CellBorders borders = new CellBorders();

        borders.topBorderProperty().setValue(topButton.isSelected());
        borders.rightBorderProperty().setValue(rightButton.isSelected());
        borders.bottomBorderProperty().setValue(bottomButton.isSelected());
        borders.leftBorderProperty().setValue(leftButton.isSelected());

        borders.topOpacityProperty().setValue(topSpinner.getValue());
        borders.rightOpacityProperty().setValue(rightSpinner.getValue());
        borders.bottomOpacityProperty().setValue(bottomSpinner.getValue());
        borders.leftOpacityProperty().setValue(leftSpinner.getValue());

        borders.topColorProperty().setValue(topColorPicker.getValue());
        borders.rightColorProperty().setValue(rightColorPicker.getValue());
        borders.bottomColorProperty().setValue(bottomColorPicker.getValue());
        borders.leftColorProperty().setValue(leftColorPicker.getValue());

        return borders;
    }

}
