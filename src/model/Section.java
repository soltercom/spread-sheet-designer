package model;

public class Section {

    private final int start;
    private final int end;
    private String name;

    public Section(int start, int end, String name) {
        this.start = start;
        this.end = end;
        this.name = name;
    }

    public Section (Section section) {
        start = section.getStart();
        end = section.getEnd();
        name = section.getName();
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Section add(int num) {
        if (num == (start-1))
            return new Section(num, getEnd(), getName());
        if (num == (end+1))
            return new Section(getStart(), num, getName());
        else
            return new Section(num, num, getName());
    }

    public static Section emptySection(String name) {
        return new Section(-2, -2, name);
    }

    @Override
    public String toString() {
        return "Section{" +
                "start=" + start +
                ", end=" + end +
                ", name='" + name + '\'' +
                '}';
    }
}
