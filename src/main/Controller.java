package main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import view.SpreadSheetView;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    VBox root;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        root.getChildren().add(new SpreadSheetView());
    }
}
