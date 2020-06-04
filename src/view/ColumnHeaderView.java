package view;

import controller.HeaderController;
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
import main.Controller;
import model.ColumnHeader;

import java.util.ArrayList;
import java.util.List;

public class ColumnHeaderView extends GridPane implements HeaderView {

    private static final PseudoClass SELECTED_CLASS = PseudoClass.getPseudoClass("selected");

    private final HeaderController controller;
    private final SpreadSheetView parentView;

    private final TextField[] headers;
    private final List<TextField> sections = new ArrayList<>();
    private final ContextMenu headersContextMenu = new ContextMenu();

    public ColumnHeaderView(ColumnHeader model, SpreadSheetView parentView) {
        controller = new HeaderController(model, this);
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
    }

    private void setBindings() {
        parentView.hScrollBarMaxProperty().bind(controller.calculateLengthProperty().subtract(parentView.spreadSheetWidth));
        parentView.hScrollBarValueProperty().addListener((o, v1, v2) -> redraw(v2.doubleValue()));
        parentView.spreadSheetWidth.addListener(inv -> redraw(parentView.hScrollBarValueProperty().get()));

        parentView.focusedViewPartProperty().addListener((o, oldValue, newValue) -> {
            if (!newValue.equals(SpreadSheetView.ViewParts.COLUMN_HEADER) && controller.hasSelectedSection())
                controller.unsetSelectedSection();
        });
    }

    private void onLineDragged(MouseEvent event) {
        setCursor(Cursor.H_RESIZE);
        Line line = (Line)event.getSource();
        double dW = event.getX() - line.getStartX();
        int index = (int)line.getUserData();
        controller.addLength(index, dW);
    }

    public void redraw(double dx) {
        setClip(new Rectangle(dx, 0, parentView.spreadSheetWidth.get(), 2*parentView.COLUMN_HEADER_HEIGHT + parentView.BORDER_WIDTH));
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
            createHeadersContextMenu((int)((TextField)e.getSource()).getUserData());
            headersContextMenu.show((TextField)e.getSource(), Side.BOTTOM, 0, 0);
        }
    }

    private void onSectionMousePressed(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() > 1) {
            TextField textField = (TextField) e.getSource();
            String newName = controller.editSectionName(textField.getText());
            if (newName != null) {
                textField.setText(newName);
                textField.setUserData(newName);
            }
        }
    }

    private void addHeaderConstraints(int index) {
        DoubleProperty widthProperty = controller.getLengthProperty(index);
        ColumnConstraints constraints = new ColumnConstraints(widthProperty.get());
        constraints.prefWidthProperty().bind(widthProperty);
        constraints.minWidthProperty().bind(widthProperty);
        constraints.maxWidthProperty().bind(widthProperty);
        getColumnConstraints().add(constraints);
    }

    private void addLineConstraints() {
        getColumnConstraints().add(new ColumnConstraints(parentView.BORDER_WIDTH));
    }

    public void createSection(int start, int end, String name) {
        TextField textField = new TextField(name);
        textField.setAlignment(Pos.CENTER_LEFT);
        textField.setEditable(false);
        textField.setBorder(null);
        textField.setUserData(name);
        textField.setOnMousePressed(this::onSectionMousePressed);
        textField.getStyleClass().add("column-section");
        sections.add(textField);
        add(textField,2*start+1, 0, 2*(1+end-start), 1);
    }

    public void removeSection(String name) {
        sections.stream()
            .filter(item -> item.getUserData().equals(name))
            .findFirst()
            .ifPresent(textField -> {
                sections.remove(textField);
                getChildren().remove(textField);
            });
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

    private void createHeadersContextMenu(int columnIndex) {
        headersContextMenu.getItems().clear();
        if (controller.hasSelectedSection()) {
            MenuItem item = new MenuItem("Создать секцию");
            item.setOnAction(e -> controller.addSelectedSection());
            headersContextMenu.getItems().add(item);
        }
        if (controller.isCellInSection(columnIndex)) {
            MenuItem item = new MenuItem("Удалить секцию");
            item.setOnAction(e -> controller.removeSection(columnIndex));
            headersContextMenu.getItems().add(item);
        }
    }

    public void updatePseudoClassHeaderSelected(int index, boolean state) {
        headers[index].pseudoClassStateChanged(SELECTED_CLASS, state);
    }

    @Override
    public String getUserAgentStylesheet() {
        return SpreadSheetView.class.getResource("style.css").toExternalForm();
    }

}
