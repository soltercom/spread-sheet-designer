package view;

import controller.ColumnHeaderController;
import javafx.beans.property.DoubleProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import model.ColumnHeader;

public class ColumnHeaderView extends GridPane {

    private static final PseudoClass SELECTED_CLASS = PseudoClass.getPseudoClass("selected");

    private final ColumnHeaderController controller;
    private final SpreadSheetView parentView;

    private final TextField[] headers;
    private final ContextMenu headersContextMenu = new ContextMenu();

    public ColumnHeaderView(ColumnHeader model, SpreadSheetView parentView) {
        controller = new ColumnHeaderController(model, this);
        this.parentView = parentView;
        headers = new TextField[model.size()];
        init();
        setBindings();
    }

    private void init() {
        addLineConstraints();
        for (int i = 0; i < controller.size(); i++) {
            addHeaderConstraints(i);
            addLineConstraints();
        }
        getRowConstraints().add(new RowConstraints(parentView.COLUMN_HEADER_HEIGHT));
        getRowConstraints().add(new RowConstraints(parentView.COLUMN_HEADER_HEIGHT));

        for (int i = 0; i < controller.size(); i++) {
            add(createHeader(i),2*i+1, 1, 1, 1);
            add(createLine(i), 2*i+2, 1, 1, 1);
        }

        redraw(0);

        createHeadersContextMenu();
    }

    private void setBindings() {
        parentView.hScrollBarMaxProperty().bind(controller.calculateWidthProperty().subtract(parentView.spreadSheetWidth));
        parentView.hScrollBarValueProperty().addListener((o, v1, v2) -> redraw(v2.doubleValue()));
        parentView.spreadSheetWidth.addListener(inv -> redraw(parentView.hScrollBarValueProperty().get()));
    }

    private void onLineDragged(MouseEvent event) {
        setCursor(Cursor.H_RESIZE);
        Line line = (Line)event.getSource();
        double dW = event.getX() - line.getStartX();
        int index = (int)line.getUserData();
        controller.addColumnWidth(index, dW);
    }

    public void redraw(double dx) {
        setClip(new Rectangle(dx, parentView.COLUMN_HEADER_HEIGHT, parentView.spreadSheetWidth.get(), parentView.COLUMN_HEADER_HEIGHT));
        setTranslateX(-dx);
    }

    private void onHeaderMousePressed(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY) {
            parentView.focusedViewPartProperty().setValue(SpreadSheetView.ViewParts.COLUMN_HEADER);
            if (e.isControlDown()) {
                int index = (int) ((TextField) e.getSource()).getUserData();
                controller.setSelectedSection(index);
            } else {
                controller.unsetSelectedSection();
            }
        } else if (e.getButton() == MouseButton.SECONDARY) {
            createHeadersContextMenu();
            headersContextMenu.show((TextField)e.getSource(), Side.BOTTOM, 0, 0);
        }
    }

    private void addHeaderConstraints(int index) {
        DoubleProperty widthProperty = controller.getWidthProperty(index);
        ColumnConstraints constraints = new ColumnConstraints(widthProperty.get());
        constraints.prefWidthProperty().bind(widthProperty);
        constraints.minWidthProperty().bind(widthProperty);
        constraints.maxWidthProperty().bind(widthProperty);
        getColumnConstraints().add(constraints);
    }

    private void addLineConstraints() {
        getColumnConstraints().add(new ColumnConstraints(parentView.BORDER_WIDTH));
    }

    private TextField createHeader(int index) {
        TextField textField = new TextField(String.valueOf(index+1));
        textField.setAlignment(Pos.CENTER);
        textField.setEditable(false);
        textField.setBorder(null);
        textField.setUserData(index);
        textField.setOnMousePressed(this::onHeaderMousePressed);
        textField.getStyleClass().add("column-header");
        headers[index] = textField;
        textField.setContextMenu(headersContextMenu);
        return textField;
    }

    private Line createLine(int index) {
        Line line = new Line(0, 0, 0, parentView.COLUMN_HEADER_HEIGHT);
        line.setOnMouseEntered(e -> setCursor(Cursor.H_RESIZE));
        line.setOnMouseExited(e -> setCursor(Cursor.DEFAULT));
        line.setUserData(index);
        line.setOnMouseDragged(this::onLineDragged);
        line.getStyleClass().add("column-header-line");
        return line;
    }

    private void createHeadersContextMenu() {
        headersContextMenu.getItems().clear();
        if (controller.hasSelectedSection()) {
            MenuItem item1 = new MenuItem("Создать секцию");
            item1.setOnAction(System.out::println);
            MenuItem item2 = new MenuItem("Удалить секцию");
            item2.setOnAction(System.out::println);
            headersContextMenu.getItems().addAll(item1, item2);
        }
    }

    public void updatePseudoClassHeaderSelected(int index, boolean state) {
        headers[index].pseudoClassStateChanged(SELECTED_CLASS, state);
    }


    @Override
    public String getUserAgentStylesheet() {
        return SpreadSheetView.class.getResource("style.css").toExternalForm();
    }

    public SpreadSheetView getParentView() {
        return parentView;
    }

}
