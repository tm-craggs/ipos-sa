package cat;

import db.DatabaseManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class CatalougeManager {

    @FXML private TableView<CatalogueItem> tableView;
    @FXML private TextField searchField;
    @FXML private Label warningLabel;
    @FXML private Label totalItemsLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label selectedLabel;
    @FXML private Button editItemButton;
    @FXML private Button deleteItemButton;

    @FXML
    public void initialize() {
        searchField.textProperty().addListener((obs, old, newVal) -> filterTable(newVal));
        loadCatalogue();

        editItemButton.setDisable(true);

        // selected item
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                selectedLabel.setText("Selected: " + newItem.getDescription());
                editItemButton.setDisable(false);
            } else {
                selectedLabel.setText("Selected: None");
                editItemButton.setDisable(true);
            }
        });


    }

    private void loadCatalogue() {
        tableView.getItems().clear();
        List<CatalogueItem> items = DatabaseManager.getCatalogueItems();
        tableView.getItems().addAll(items);
        totalItemsLabel.setText("Total items: " + items.size());
    }

    private void filterTable(String query) {
        tableView.getItems().clear();
        List<CatalogueItem> items = DatabaseManager.getCatalogueItems();

        if(query == null || query.isEmpty()) {
            tableView.getItems().addAll(items);
        }
        else {
            String l = query.toLowerCase();
            for(CatalogueItem item : items) {
                if(item.getDescription().toLowerCase().contains(l)
                        || String.valueOf(item.getItemId()).contains(l)
                        || item.getPackageType().toLowerCase().contains(l)
                        || item.getUnit().toLowerCase().contains(l)
                ) {tableView.getItems().add(item);}
            }
        }
        totalItemsLabel.setText("Total items: " + items.size());
    }

    @FXML
    private void handleDeleteItem() {
        CatalogueItem selected = tableView.getSelectionModel().getSelectedItem();
        if(selected == null) {
            warningLabel.setText("please select an item before deleting it");
            return;
        }
        int id = selected.getItemId();
        DatabaseManager.deleteCatalogueItem(id);
        loadCatalogue();
    }

    @FXML
    private void handleEditItem() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Item");

        ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField description = new TextField();
        TextField packageType = new TextField();
        TextField unit = new TextField();
        TextField unitsPerPack = new TextField();
        TextField packageCost = new TextField();
        TextField availability = new TextField();
        TextField stockLimit = new TextField();

        grid.add(new Label("Description:"), 0, 1);   grid.add(description, 1, 1);
        grid.add(new Label("Package Type:"), 0, 2);  grid.add(packageType, 1, 2);
        grid.add(new Label("Unit:"), 0, 3);          grid.add(unit, 1, 3);
        grid.add(new Label("Units/Pack:"), 0, 4);    grid.add(unitsPerPack, 1, 4);
        grid.add(new Label("Package Cost:"), 0, 5);  grid.add(packageCost, 1, 5);
        grid.add(new Label("Availability:"), 0, 6);  grid.add(availability, 1, 6);
        grid.add(new Label("Stock Limit:"), 0, 7);   grid.add(stockLimit, 1, 7);


        dialog.getDialogPane().setContent(grid);

        CatalogueItem selected = tableView.getSelectionModel().getSelectedItem();
        int id = selected.getItemId();

        description.setText(selected.getDescription());
        packageType.setText(selected.getPackageType());
        unit.setText(selected.getUnit());
        unitsPerPack.setText(String.valueOf(selected.getUnitsPerPack()));
        packageCost.setText(String.valueOf(selected.getPackageCost()));
        availability.setText(String.valueOf(selected.getAvailability()));
        stockLimit.setText(String.valueOf(selected.getStockLimit()));

        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == confirmButton) {
                DatabaseManager.updateDescription(id, description.getText());
                DatabaseManager.updatePackageType(id, packageType.getText());
                DatabaseManager.updateUnit(id, unit.getText());
                DatabaseManager.updateUnitsPerPack(id, Integer.parseInt(unitsPerPack.getText()));
                DatabaseManager.updatePackageCost(id, Double.parseDouble(packageCost.getText()));
                DatabaseManager.updateAvailability(id, Integer.parseInt(availability.getText()));
                DatabaseManager.updateStockLimit(id, Integer.parseInt(stockLimit.getText()));
                loadCatalogue();
            }
        });
    }

    @FXML
    private void handleAddItem() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Item");
        dialog.setHeaderText("Enter new catalogue item details");

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField itemId = new TextField();
        TextField description = new TextField();
        TextField packageType = new TextField();
        TextField unit = new TextField();
        TextField unitsPerPack = new TextField();
        TextField packageCost = new TextField();
        TextField availability = new TextField();
        TextField stockLimit = new TextField();

        grid.add(new Label("Item ID:"), 0, 0);       grid.add(itemId, 1, 0);
        grid.add(new Label("Description:"), 0, 1);   grid.add(description, 1, 1);
        grid.add(new Label("Package Type:"), 0, 2);  grid.add(packageType, 1, 2);
        grid.add(new Label("Unit:"), 0, 3);          grid.add(unit, 1, 3);
        grid.add(new Label("Units/Pack:"), 0, 4);    grid.add(unitsPerPack, 1, 4);
        grid.add(new Label("Package Cost:"), 0, 5);  grid.add(packageCost, 1, 5);
        grid.add(new Label("Availability:"), 0, 6);  grid.add(availability, 1, 6);
        grid.add(new Label("Stock Limit:"), 0, 7);   grid.add(stockLimit, 1, 7);

        itemId.setPromptText("Integer e.g. 100001");
        description.setPromptText("String e.g. Paracetamol");
        packageType.setPromptText("String e.g. box");
        unit.setPromptText("String e.g. Caps");
        unitsPerPack.setPromptText("Integer e.g. 20");
        packageCost.setPromptText("Double e.g. 0.10");
        availability.setPromptText("Integer e.g. 10000");
        stockLimit.setPromptText("Integer e.g. 300");

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == addButton) {
                try {
                    int avail = Integer.parseInt(availability.getText());
                    int stock = Integer.parseInt(stockLimit.getText());

                    DatabaseManager.addCatalogueItem(
                            Integer.parseInt(itemId.getText()),
                            description.getText(),
                            packageType.getText(),
                            unit.getText(),
                            Integer.parseInt(unitsPerPack.getText()),
                            Double.parseDouble(packageCost.getText()),
                            avail,
                            stock
                    );
                    loadCatalogue();
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText("Please check your inputs");
                    alert.showAndWait();
                }
            }
        });
    }

    @FXML
    private void handleMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ipos/sa/main-menu.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("IPOS Main Menu");
            stage.setScene(new Scene(root));
            stage.show();
            Stage current = (Stage) tableView.getScene().getWindow();
            current.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}