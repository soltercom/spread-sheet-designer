package controller;

import javafx.application.Platform;
import model.ColumnHeader;
import model.RowHeader;
import model.Section;
import view.ColumnHeaderView;
import view.RowHeaderView;

public class RowHeaderController {

    private final RowHeader model;
    private final RowHeaderView view;

    private Section selectedSection;

    public RowHeaderController(RowHeader model, RowHeaderView view) {
        this.model = model;
        this.view = view;
        Platform.runLater(this::init);
    }

    private void init() {

    }


}
