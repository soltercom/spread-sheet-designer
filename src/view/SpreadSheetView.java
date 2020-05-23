package view;

import controller.CellSheetController;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Rectangle;
import model.Column;
import model.SpreadSheet;

public class SpreadSheetView extends GridPane {

    private static final double ROW_HEADER_WIDTH = 75.0D;
    private static final double COLUMN_HEADER_HEIGHT = 25.0D;
    private static final double SCROLL_BAR_WIDTH = 20.0D;
    private static final double BORDER_WIDTH = 1.0D;
    private static final double SPREAD_SHEET_HEIGHT = 600.0D;
    private static final double SPREAD_SHEET_WIDTH = 1000.0D;

    private final ScrollBar hScrollBar = new ScrollBar();
    private final ScrollBar vScrollBar = new ScrollBar();

    private final ColumnHeaderView columnHeaderView;
    private final RowHeaderView rowHeaderView;
    private final CellSheetView cellSheetView;

    private final SpreadSheet model;

    public SpreadSheetView() {
        model = new SpreadSheet();
        columnHeaderView = new ColumnHeaderView(model.getColumnHeader(), hScrollBar);
        rowHeaderView = new RowHeaderView(model.getRowHeader(), vScrollBar);
        cellSheetView = new CellSheetView(new CellSheetController(model), hScrollBar, vScrollBar);
        init();
    }

    private void init() {

        getColumnConstraints().add(new ColumnConstraints(ROW_HEADER_WIDTH));
        getColumnConstraints().add(new ColumnConstraints(SPREAD_SHEET_WIDTH));
        getColumnConstraints().add(new ColumnConstraints(SCROLL_BAR_WIDTH));

        getRowConstraints().add(new RowConstraints(COLUMN_HEADER_HEIGHT));
        getRowConstraints().add(new RowConstraints(SPREAD_SHEET_HEIGHT));
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

    }

    @Override
    public String getUserAgentStylesheet() {
        return SpreadSheetView.class.getResource("style.css").toExternalForm();
    }

}
