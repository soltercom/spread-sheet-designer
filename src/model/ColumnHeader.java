package model;

import form.validator.Validators;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public String getSectionName(int columnIndex) {
       return sectionList.stream()
           .filter(item -> hitTest(item, columnIndex))
           .map(Section::getName)
           .findFirst().orElse("");
    }

    public Section getSectionByName(String name) {
        return sectionList.stream()
                .filter(item -> item.getName().equals(name))
                .findFirst().orElse(null);
    }

    private boolean hitTest(Section item, int columnIndex) {
        return item.getStart() <= columnIndex && columnIndex <= item.getEnd();
    }

    public boolean isSectionValid(Section section) {
        if (!Validators.NAME.validate(section.getName())) return false;
        if (!checkSectionName(section.getName())) return false;
        if (sectionList.stream()
                .anyMatch(item -> hitTest(item, section.getStart()))) return false;
        if (sectionList.stream()
                .anyMatch(item -> hitTest(item, section.getEnd()))) return false;
        return true;
    }

    public boolean addSection(Section section) {
        if (!isSectionValid(section)) return false;
        return sectionList.add(new Section(section));
    }

    public boolean removeSection(int columnIndex) {
        if (!isColumnInSection(columnIndex)) return false;
        return sectionList.removeIf(item -> hitTest(item, columnIndex));
    }

    public boolean isColumnInSection(int columnIndex) {
        return sectionList.stream()
            .anyMatch(item -> hitTest(item, columnIndex));
    }

    public boolean removeSection(Section section) {
        return sectionList.remove(section);
    }

}
