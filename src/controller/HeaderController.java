package controller;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.TextInputDialog;
import model.Header;
import model.Section;
import view.HeaderView;

import java.util.Optional;

public class HeaderController {

    private final Header model;
    private final HeaderView view;

    private Section selectedSection;

    public HeaderController(Header model, HeaderView view) {
        this.model = model;
        this.view = view;
        Platform.runLater(this::init);
    }

    private void init() {
        unsetSelectedSection();
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
        Optional<String> name = sectionNameDialog.showAndWait();
        if (name.isPresent()) {
            if (model.checkSectionName(name.get())) section.setName(name.get());
            return section.getName();
        } else {
            return null;
        }
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
            if (editSectionName(selectedSection) != null && model.addSection(selectedSection)) {
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

    public boolean isCellInSection(int index) {
        return model.isColumnInSection(index);
    }

    public int size() {
        return model.size();
    }
    public DoubleBinding calculateLengthProperty() {
        return model.calculateLengthProperty();
    }
    public void addLength(int index, double dL) {
        model.addLength(index, dL);
    }
    public DoubleProperty getLengthProperty(int index) {
        return model.getLengthProperty(index);
    }
    public DoubleProperty getBorderLengthProperty() {
        return model.getBorderLengthProperty();
    }
    public DoubleBinding getRangeLengthBinding(int start, int end) {
        DoubleBinding calculateLength = new SimpleDoubleProperty(0.0D).add(getBorderLengthProperty());
        for (int index = start; index <= end ; index++) {
            calculateLength = calculateLength.add(getLengthProperty(index))
                                             .add(getBorderLengthProperty());
        }
        return calculateLength;
    }
    public boolean hasSelectedSection() { return selectedSection != null && selectedSection.getStart() > -1; }
}
