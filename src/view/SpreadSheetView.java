package view;

import form.Form;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import model.SpreadSheet;

public class SpreadSheetView extends GridPane {

    public enum ViewParts {
        COLUMN_HEADER,
        ROW_HEADER,
        FORM,
        CELL_SHEET
    }

    public final double ROW_HEADER_WIDTH = 75.0D;
    public final double ROW_HEADER_HEIGHT = 25.0D;
    public final double COLUMN_HEADER_WIDTH = 75.0D;
    public final double COLUMN_HEADER_HEIGHT = 25.0D;
    public final double SCROLL_BAR_WIDTH = 20.0D;
    public final double BORDER_WIDTH = 1.0D;
    public final double FORM_WIDTH = 300.0D;

    public final DoubleProperty spreadSheetWidth = new SimpleDoubleProperty(0.0D);
    public final DoubleProperty spreadSheetHeight = new SimpleDoubleProperty(0.0D);

    private final ScrollBar hScrollBar = new ScrollBar();
    private final ScrollBar vScrollBar = new ScrollBar();

    private final ColumnHeaderView columnHeaderView;
    private final RowHeaderView rowHeaderView;
    private final CellSheetView cellSheetView;
    private final Form formView;

    private final ObjectProperty<ViewParts> focusedViewPart = new SimpleObjectProperty<>();

    private final SpreadSheet model;

    public SpreadSheetView() {
        model = new SpreadSheet();
        columnHeaderView = new ColumnHeaderView(model.getColumnHeader(), this);
        rowHeaderView = new RowHeaderView(model.getRowHeader(), this);
        cellSheetView = new CellSheetView(model, this);
        formView = new Form(this);
        init();
        setBindings();
    }

    private void init() {

        getColumnConstraints().add(new ColumnConstraints(ROW_HEADER_WIDTH));
        ColumnConstraints constraintsSpreadSheetWidth = new ColumnConstraints();
        constraintsSpreadSheetWidth.maxWidthProperty().bind(spreadSheetWidth);
        constraintsSpreadSheetWidth.minWidthProperty().bind(spreadSheetWidth);
        constraintsSpreadSheetWidth.prefWidthProperty().bind(spreadSheetWidth);
        getColumnConstraints().add(constraintsSpreadSheetWidth);
        getColumnConstraints().add(new ColumnConstraints(SCROLL_BAR_WIDTH));
        getColumnConstraints().add(new ColumnConstraints(FORM_WIDTH));

        getRowConstraints().add(new RowConstraints(COLUMN_HEADER_HEIGHT * 2));
        RowConstraints constraintsSpreadSheetHeight = new RowConstraints();
        constraintsSpreadSheetHeight.maxHeightProperty().bind(spreadSheetHeight);
        constraintsSpreadSheetHeight.minHeightProperty().bind(spreadSheetHeight);
        constraintsSpreadSheetHeight.prefHeightProperty().bind(spreadSheetHeight);
        getRowConstraints().add(constraintsSpreadSheetHeight);
        getRowConstraints().add(new RowConstraints(SCROLL_BAR_WIDTH));

        add(columnHeaderView, 1, 0, 1, 1);
        hScrollBar.setOrientation(Orientation.HORIZONTAL);
        hScrollBar.setMin(0.0D);
        hScrollBar.setUnitIncrement(1.0D);
        hScrollBar.setBlockIncrement(1.0D);
        add(hScrollBar, 1, 2, 1, 1);

        add(rowHeaderView, 0, 1, 1, 1);
        setValignment(rowHeaderView, VPos.TOP);
        vScrollBar.setOrientation(Orientation.VERTICAL);
        vScrollBar.setMin(0.0D);
        vScrollBar.setUnitIncrement(1.0D);
        vScrollBar.setBlockIncrement(1.0D);
        add(vScrollBar, 2, 1, 1, 1);

        add(cellSheetView, 1, 1, 1, 1);
        setValignment(cellSheetView, VPos.TOP);

        add(formView, 3, 1, 1, 3);

    }

    private void setBindings() {
        Platform.runLater(() -> {
            spreadSheetWidth.bind(getScene().widthProperty()
                    .subtract(ROW_HEADER_WIDTH)
                    .subtract(SCROLL_BAR_WIDTH)
                    .subtract(BORDER_WIDTH)
                    .subtract(FORM_WIDTH));
            spreadSheetHeight.bind(getScene().heightProperty()
                    .subtract(COLUMN_HEADER_HEIGHT*2)
                    .subtract(SCROLL_BAR_WIDTH)
                    .subtract(BORDER_WIDTH));
        });
    }

    public DoubleProperty hScrollBarValueProperty() {
        return hScrollBar.valueProperty();
    }
    public DoubleProperty vScrollBarValueProperty() {
        return vScrollBar.valueProperty();
    }
    public DoubleProperty hScrollBarMaxProperty() {
        return hScrollBar.maxProperty();
    }
    public DoubleProperty vScrollBarMaxProperty() {
        return vScrollBar.maxProperty();
    }

    public Form getFormView() {
        return formView;
    }

    public void beforeEditCell() { cellSheetView.beforeEditCell(); }
    public void afterEditCell() { cellSheetView.afterEditCell(); }

    @Override
    public String getUserAgentStylesheet() {
        return SpreadSheetView.class.getResource("style.css").toExternalForm();
    }

    public ObjectProperty<ViewParts> focusedViewPartProperty() { return focusedViewPart; }

}
