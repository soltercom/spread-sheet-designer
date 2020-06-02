package controller;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.TextInputDialog;
import model.ColumnHeader;
import model.Section;
import view.ColumnHeaderView;
import view.SpreadSheetView;

public class ColumnHeaderController {

    private final ColumnHeader model;
    private final ColumnHeaderView view;

    private Section selectedSection;

    public ColumnHeaderController(ColumnHeader model, ColumnHeaderView view) {
        this.model = model;
        this.view = view;
        Platform.runLater(this::init);
    }

    private void init() {
        unsetSelectedSection();
        view.getParentView().focusedViewPartProperty().addListener((o, oldValue, newValue) -> {
            if (!newValue.equals(SpreadSheetView.ViewParts.COLUMN_HEADER) && hasSelectedSection())
                unsetSelectedSection();
        });
    }

    private void updatePseudoClassHeaderSelected(boolean state) {
        if (hasSelectedSection())
            for (int i = selectedSection.getStart(); i <= selectedSection.getEnd(); i++)
                view.updatePseudoClassHeaderSelected(i, state);
    }

    public void unsetSelectedSection() {
        updatePseudoClassHeaderSelected(false);
        int counter = 1;
        String sectionName = "Область";
        while (!model.checkSectionName(sectionName + counter)) counter++;
        selectedSection = Section.emptySection("Область" + counter);
    }

    public void setSelectedSection(int index) {
        updatePseudoClassHeaderSelected(false);
        selectedSection = selectedSection.add(index);
        updatePseudoClassHeaderSelected(true);
    }

    public String editSectionName(Section section) {
        TextInputDialog sectionNameDialog = new TextInputDialog(section.getName());
        sectionNameDialog.setTitle("Задайте имя области");
        sectionNameDialog.setContentText("Имя области:");
        sectionNameDialog.setHeaderText("");
        sectionNameDialog.showAndWait()
            .ifPresent(name -> {
                if (model.checkSectionName(name)) section.setName(name);
            });
        return section.getName();
    }

    public String editSectionName(String name) {
        Section section = model.getSectionByName(name);
        if (section != null)
            return editSectionName(section);
        else
            return null;
    }

    public void addSelectedSection() {
        if (model.isSectionValid(selectedSection)) {
            editSectionName(selectedSection);
            if (model.addSection(selectedSection)) {
                view.createSection(selectedSection.getStart(), selectedSection.getEnd(), selectedSection.getName());
                unsetSelectedSection();
            }
        }
    }

    public void removeSection(int columnIndex) {
        String name = model.getSectionName(columnIndex);
        if (model.removeSection(columnIndex) && !name.isEmpty()) {
            view.removeSection(name);
        }
    }

    public boolean isColumnInSection(int index) {
        return model.isColumnInSection(index);
    }

    public int size() {
        return model.size();
    }
    public DoubleBinding calculateWidthProperty() {
        return model.calculateWidthProperty();
    }
    public void addColumnWidth(int index, double dW) {
        model.addColumnWidth(index, dW);
    }
    public DoubleProperty getWidthProperty(int index) {
        return model.get(index).widthProperty();
    }
    public boolean hasSelectedSection() { return selectedSection != null && selectedSection.getStart() > -1; }
}
