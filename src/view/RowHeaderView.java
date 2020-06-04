package view;

import controller.HeaderController;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import model.RowHeader;
import org.jetbrains.annotations.NotNull;

import javax.script.Bindings;
import java.util.ArrayList;
import java.util.List;

public class RowHeaderView extends GridPane implements HeaderView {

    private static final PseudoClass SELECTED_CLASS = PseudoClass.getPseudoClass("selected");

    private final HeaderController controller;
    private final SpreadSheetView parentView;

    private final TextField[] headers;
    private final List<TextField> sections = new ArrayList<>();
    private final ContextMenu headersContextMenu = new ContextMenu();

    public RowHeaderView(RowHeader model, SpreadSheetView parentView) {
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
        getColumnConstraints().add(new ColumnConstraints(parentView.ROW_HEADER_WIDTH));
        getColumnConstraints().add(new ColumnConstraints(parentView.ROW_HEADER_WIDTH));

        for (int i = 0; i < controller.size(); i++) {
            add(createHeader(i),1, 2*i+1, 1, 1);
            add(createLine(i), 1, 2*i+2, 1, 1);
        }

        redraw(0);
    }

    private void setBindings() {
        parentView.vScrollBarMaxProperty().bind(controller.calculateLengthProperty().subtract(parentView.spreadSheetHeight));
        parentView.vScrollBarValueProperty().addListener((o, v1, v2) -> redraw(v2.doubleValue()));
        parentView.spreadSheetHeight.addListener(inv -> redraw(parentView.vScrollBarValueProperty().get()));

        parentView.focusedViewPartProperty().addListener((o, oldValue, newValue) -> {
            if (!newValue.equals(SpreadSheetView.ViewParts.ROW_HEADER) && controller.hasSelectedSection())
                controller.unsetSelectedSection();
        });
    }

    private void onLineDragged(MouseEvent event) {
        setCursor(Cursor.V_RESIZE);
        Line line = (Line)event.getSource();
        double dW = event.getY() - line.getStartY();
        int index = (int)line.getUserData();
        controller.addLength(index, dW);
    }

    public void redraw(double dy) {
        setClip(new Rectangle(0, dy, parentView.ROW_HEADER_WIDTH*2, parentView.spreadSheetHeight.get()));
        setTranslateY(-dy);
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
        DoubleProperty heightProperty = controller.getLengthProperty(index);
        RowConstraints constraints = new RowConstraints(heightProperty.get());
        constraints.prefHeightProperty().bind(heightProperty);
        constraints.minHeightProperty().bind(heightProperty);
        constraints.maxHeightProperty().bind(heightProperty);
        getRowConstraints().add(constraints);
    }

    private void addLineConstraints() {
        getRowConstraints().add(new RowConstraints(parentView.BORDER_WIDTH));
    }

    public void createSection(int start, int end, String name) {
        DoubleBinding sectionHeight = controller.getRangeLengthBinding(start, end);

        TextField textField = new TextField(name);
        textField.setAlignment(Pos.CENTER_LEFT);
        textField.setEditable(false);
        textField.setBorder(null);
        textField.setUserData(name);
        textField.setOnMousePressed(this::onSectionMousePressed);
        textField.getStyleClass().add("row-section");
        textField.maxHeightProperty().bind(sectionHeight);
        textField.prefHeightProperty().bind(sectionHeight);
        textField.minHeightProperty().bind(sectionHeight);
        sections.add(textField);
        add(textField,0, 2*start+1, 1, 2*(1+end-start));
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
        textField.maxHeightProperty().bind(controller.getLengthProperty(index));
        textField.prefHeightProperty().bind(controller.getLengthProperty(index));
        textField.minHeightProperty().bind(controller.getLengthProperty(index));
        textField.getStyleClass().add("row-header");
        headers[index] = textField;
        textField.setContextMenu(headersContextMenu);
        return textField;
    }

    private Line createLine(int index) {
        Line line = new Line(0, 0, parentView.ROW_HEADER_WIDTH, 0);
        line.setOnMouseEntered(e -> setCursor(Cursor.V_RESIZE));
        line.setOnMouseExited(e -> setCursor(Cursor.DEFAULT));
        line.setUserData(index);
        line.setOnMouseDragged(this::onLineDragged);
        line.getStyleClass().add("row-header-line");
        return line;
    }

    private void createHeadersContextMenu(int rowIndex) {
        headersContextMenu.getItems().clear();
        if (controller.hasSelectedSection()) {
            MenuItem item = new MenuItem("Создать секцию");
            item.setOnAction(e -> controller.addSelectedSection());
            headersContextMenu.getItems().add(item);
        }
        if (controller.isCellInSection(rowIndex)) {
            MenuItem item = new MenuItem("Удалить секцию");
            item.setOnAction(e -> controller.removeSection(rowIndex));
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
