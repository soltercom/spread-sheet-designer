package model;

import form.validator.Validators;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;

import java.util.ArrayList;
import java.util.List;

public abstract class Header {

    protected final List<Section> sectionList = new ArrayList<>();

    public Header() {}

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

    public abstract int size();
    public abstract DoubleBinding calculateLengthProperty();
    public abstract void addLength(int index, double dL);
    public abstract DoubleProperty getLengthProperty(int index);
    public abstract DoubleProperty getBorderLengthProperty();
}
