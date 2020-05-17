package model;

import java.util.ArrayList;
import java.util.List;

public class Area {

    private final List<Cell> cells;

    public Area() {
        cells = new ArrayList<>();
    }

    public Area(List<Cell> cells) {
        this.cells = cells;
    }
}
