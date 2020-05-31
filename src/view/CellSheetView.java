package view;

import controller.CellSheetController;
import javafx.application.Platform;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import model.Cell;
import model.CellBorders;
import model.SpreadSheet;

public class CellSheetView extends GridPane {

    private static final PseudoClass EDIT_CELL_CLASS = PseudoClass.getPseudoClass("edit");

    private final CellSheetController controller;
    private final SpreadSheetView parentView;

    private final TextField[][] cells;
    private final Line[][] hLines;
    private final Line[][] vLines;

    public CellSheetView(SpreadSheet model, SpreadSheetView parentView) {
        this.controller = new CellSheetController(model, this);
        this.parentView = parentView;
        this.cells = new TextField[controller.getMaxRow()][controller.getMaxColumn()];
        this.hLines = new Line[controller.getMaxRow()+1][controller.getMaxColumn()];
        this.vLines = new Line[controller.getMaxRow()][controller.getMaxColumn()+1];
        init();
        setBindings();
    }

    private void init() {
        getRowConstraints().add(new RowConstraints(parentView.BORDER_WIDTH));
        for (int i = 0; i < controller.getMaxRow(); i++) {
            addRowConstraint(i);
            getRowConstraints().add(new RowConstraints(parentView.BORDER_WIDTH));
        }
        getColumnConstraints().add(new ColumnConstraints(parentView.BORDER_WIDTH));
        for (int i = 0; i < controller.getMaxColumn(); i++) {
            addColumnConstraint(i);
            getColumnConstraints().add(new ColumnConstraints(parentView.BORDER_WIDTH));
        }

        for (int i = 0; i < controller.getMaxRow(); i++) {
            Line vLine = createVLine(i, 0);
            vLines[i][0] = vLine;
            add(vLine, 0, i*2+1, 1, 1);
        }


        for (int i = 0; i < controller.getMaxColumn(); i++) {
            Line hLine = createHLine(0, i);
            hLines[0][i] = hLine;
            add(hLine, i*2+1, 0, 1, 1);
        }


        for (int i = 0; i < controller.getMaxRow(); i++) {
            for (int j = 0; j < controller.getMaxColumn(); j++) {
                add(createCell(i, j), j*2+1, i*2+1, 1, 1);
                Line hLine = createHLine(i, j);
                hLines[i+1][j] = hLine;
                add(hLine, j*2+1, i*2+2, 1, 1);
                Line vLine = createVLine(i, j);
                vLines[i][j+1] = vLine;
                add(vLine, j*2+2, i*2+1, 1, 1);
            }
        }

        redraw();
    }

    private TextField getCurrentCell() {
        if (getScene() != null)
            return (TextField) getScene().lookup("#" + controller.getCurrentId());
        else
            return null;
    }

    public void setFocusedCell() {
        TextField cell = getCurrentCell();
        if (cell != null)
            cell.requestFocus();
    }

    private void setBindings() {
        parentView.hScrollBarValueProperty().addListener(inv -> redraw());
        parentView.vScrollBarValueProperty().addListener(inv -> redraw());
        parentView.spreadSheetWidth.addListener(inv -> redraw());
        parentView.spreadSheetHeight.addListener(inv -> redraw());

        int maxCol = controller.getMaxColumn();
        int maxRow = controller.getMaxRow();
        for (int col = 0; col < maxCol; col++) {
            CellBorders topCellBorders = ((Cell)cells[0][col].getUserData()).bordersProperty().getValue();
            hLines[0][col].visibleProperty().bind(topCellBorders.topBorderProperty());
            hLines[0][col].strokeProperty().bind(topCellBorders.topColorProperty());

            CellBorders bottomCellBorders = ((Cell)cells[maxRow-1][col].getUserData()).bordersProperty().getValue();
            hLines[maxRow][col].visibleProperty().bind(bottomCellBorders.bottomBorderProperty());
            hLines[maxRow][col].strokeProperty().bind(bottomCellBorders.bottomColorProperty());
        }

        for (int row = 0; row < maxRow; row++) {
            CellBorders leftCellBorders = ((Cell)cells[row][0].getUserData()).bordersProperty().getValue();
            vLines[row][0].visibleProperty().bind(leftCellBorders.leftBorderProperty());
            vLines[row][0].strokeProperty().bind(leftCellBorders.leftColorProperty());

            CellBorders rightCellBorders = ((Cell)cells[row][maxCol-1].getUserData()).bordersProperty().getValue();
            vLines[row][maxCol].visibleProperty().bind(rightCellBorders.rightBorderProperty());
            vLines[row][maxCol].strokeProperty().bind(rightCellBorders.rightColorProperty());
        }

        for (int row = 1; row < maxRow; row++) {
            for (int col = 0; col < maxCol; col++) {
                CellBorders topCellBorders = ((Cell)cells[row-1][col].getUserData()).bordersProperty().getValue();
                CellBorders bottomCellBorders = ((Cell)cells[row][col].getUserData()).bordersProperty().getValue();

                hLines[row][col].visibleProperty()
                    .bind(topCellBorders.bottomBorderProperty().or(bottomCellBorders.topBorderProperty()));
            }
        }

        for (int row = 0; row < maxRow; row++) {
            for (int col = 1; col < maxCol; col++) {
                CellBorders leftCellBorders = ((Cell)cells[row][col-1].getUserData()).bordersProperty().getValue();
                CellBorders rightCellBorders = ((Cell)cells[row][col].getUserData()).bordersProperty().getValue();

                vLines[row][col].visibleProperty()
                    .bind(leftCellBorders.rightBorderProperty().or(rightCellBorders.leftBorderProperty()));
            }
        }

    }

    public void redraw() {
        double x = parentView.hScrollBarValueProperty().doubleValue();
        double y = parentView.vScrollBarValueProperty().doubleValue();
        setClip(new Rectangle(x, y, parentView.spreadSheetWidth.get(), parentView.spreadSheetHeight.get()));
        setTranslateX(-x);
        setTranslateY(-y);
    }

    private void onCellMousePressed(MouseEvent event) {
        TextField textField = (TextField)event.getSource();
        controller.setCurrentCell((Cell)textField.getUserData());
    }

    private void onCellKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.UP)
            controller.move(-1, 0);
        else if (event.getCode() == KeyCode.RIGHT)
            controller.move(0, 1);
        else if (event.getCode() == KeyCode.DOWN)
            controller.move(1, 0);
        else if (event.getCode() == KeyCode.LEFT)
            controller.move(0, -1);
    }

    public void beforeEditCell() {
        TextField cell = getCurrentCell();
        if (cell != null)
            cell.pseudoClassStateChanged(EDIT_CELL_CLASS, true);
    }

    public void afterEditCell() {
        TextField cell = getCurrentCell();
        if (cell != null) {
            cell.setText(controller.getCurrentValue());
            cell.requestFocus();
            cell.pseudoClassStateChanged(EDIT_CELL_CLASS, false);
        }
    }

    private void addRowConstraint(int row) {
        DoubleProperty heightProperty = controller.getHeightProperty(row);
        RowConstraints constraints = new RowConstraints(heightProperty.get());
        constraints.prefHeightProperty().bind(heightProperty);
        constraints.minHeightProperty().bind(heightProperty);
        constraints.maxHeightProperty().bind(heightProperty);
        getRowConstraints().add(constraints);
    }

    private void addColumnConstraint(int col) {
        DoubleProperty widthProperty = controller.getWidthProperty(col);
        ColumnConstraints constraints = new ColumnConstraints(widthProperty.get());
        constraints.prefWidthProperty().bind(widthProperty);
        constraints.minWidthProperty().bind(widthProperty);
        constraints.maxWidthProperty().bind(widthProperty);
        getColumnConstraints().add(constraints);
    }

    TextField createCell(int row, int col) {
        TextField textField = new TextField(controller.getValue(row, col));
        this.cells[row][col] = textField;
        textField.maxHeightProperty().bind(controller.getHeightProperty(row));
        textField.prefHeightProperty().bind(controller.getHeightProperty(row));
        textField.minHeightProperty().bind(controller.getHeightProperty(row));
        textField.alignmentProperty().bind(controller.getPosProperty(row, col));
        textField.setEditable(false);
        textField.setBorder(null);
        textField.setUserData(controller.getCell(row, col));
        textField.setOnKeyPressed(this::onCellKeyPressed);
        textField.setOnMousePressed(this::onCellMousePressed);
        textField.setId(controller.getId(row, col));
        textField.getStyleClass().add("cell");
        return textField;
    }

    Line createHLine(int row, int col) {
        DoubleProperty widthProperty = controller.getWidthProperty(col);
        Line line = new Line(0, 0, widthProperty.add(1.0).get(), 0);
        line.endXProperty().bind(widthProperty.add(1.0));
        return line;
    }

    Line createVLine(int row, int col) {
        DoubleProperty heightProperty = controller.getHeightProperty(row);
        Line line = new Line(0, 0, 0, heightProperty.add(1.0).get());
        line.endYProperty().bind(heightProperty.add(1.0));
        return line;
    }

    public SpreadSheetView getParentView() {
        return parentView;
    }

    @Override
    public String getUserAgentStylesheet() {
        return SpreadSheetView.class.getResource("style.css").toExternalForm();
    }

}
