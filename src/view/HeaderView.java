package view;

public interface HeaderView {
    void updatePseudoClassHeaderSelected(int index, boolean state);
    void createSection(int start, int end, String name);
    void removeSection(String name);
}
