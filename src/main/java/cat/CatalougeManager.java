package cat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

public class CatalougeManager {

    @FXML private TableView<CatalogueItem> tableView;
    @FXML private TextField searchField;
    @FXML private Label warningLabel;
    @FXML private Label totalItemsLabel;
    @FXML private Label lowStockLabel;

    @FXML
    public void initialize() {
        searchField.textProperty().addListener((obs, old, newVal) -> filterTable(newVal));
        loadCatalogue();
    }

    private void loadCatalogue() {
        // load from database here
    }

    private void filterTable(String query) {
        // filter tableview here
    }
}
