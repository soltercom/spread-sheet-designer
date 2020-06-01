package model;

import form.validator.Validators;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.List;

public class ColumnHeader {

    private final double BORDER_WIDTH = 1.0D;

    private final List<Column> list = new ArrayList<>();
    private DoubleBinding calculateWidth;

    private final DoubleProperty width = new SimpleDoubleProperty(0.0D);

    private final List<Section> sectionList = new ArrayList<>();

    public ColumnHeader() {
        calculateWidth = width.add(BORDER_WIDTH);
    }

    public void add(Column column) {
        list.add(column);
        calculateWidth = calculateWidth.add(column.widthProperty()).add(BORDER_WIDTH);
    }

    public Column get(int index) {
        return list.get(index);
    }

    public int size() { return list.size(); }

    public DoubleProperty widthProperty() {
        return width;
    }

    public void addColumnWidth(int index, double dW) { get(index).addWidth(dW); }

    public DoubleBinding calculateWidthProperty() {
        return calculateWidth;
    }

    public boolean checkSectionName(String name) {
        return sectionList.stream().map(Section::getName)
                .noneMatch(sectionName -> sectionName.equals(name));
    }

    public boolean addSection(int start, int end, String name) {
        if (!Validators.NAME.validate(name)) return false;

        if (!checkSectionName(name)) return false;

        if (sectionList.stream()
            .anyMatch(section -> section.getStart() >= start && start <= section.getEnd())) return false;

        if (sectionList.stream()
                .anyMatch(section -> section.getStart() >= end && end <= section.getEnd())) return false;

        return sectionList.add(new Section(start, end, name));
    }

    public boolean removeSection(Section section) {
        return sectionList.remove(section);
    }

}
