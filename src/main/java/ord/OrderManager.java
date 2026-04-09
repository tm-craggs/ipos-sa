package ord;

import cat.CatalogueItem;
import db.DatabaseManager;
import ipos.sa.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

public class OrderManager {
    @FXML private TableView<CatalogueItem> catalogue;
    @FXML private TextField searchField;
    @FXML private TextField quantityField;
    @FXML private Label grandTotalLabel;
    @FXML private TableView<OrderItem> cartTable;

    @FXML private TableView<TrackedOrder> ordersTable;
    @FXML private TableView<OrderItem> orderDetailsTable;
    @FXML private Label orderInfoLabel;
    @FXML private Label balanceLabel;
    @FXML private Label creditLimitLabel;
    @FXML private Label outstandingLabel;
    @FXML private Label availableCreditLabel;
    @FXML private TableView<Invoice> invoicesTable;

    @FXML private void initialize() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {filterCatalogue(newValue);});
        unsigned(quantityField);
        loadCatalogue();
        loadOrders();
        loadBalance();
        catalogue.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        invoicesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldO, newO) -> {
            if (newO != null) {
                orderInfoLabel.setText("Order: " + newO.getOrderId());
                orderDetailsTable.getItems().clear();
                orderDetailsTable.getItems().addAll(DatabaseManager.getOrderItems(newO.getOrderId()));
            }
        });
    }

    private void unsigned(TextField i) {
        i.textProperty().addListener((obs, old, newVal) -> {
            if(newVal.contains("-")) {i.setText(newVal.replace("-",""));}
        });
    }

    private void loadCatalogue() {
        catalogue.getItems().clear();
        List<CatalogueItem> items = DatabaseManager.getCatalogueItems();
        catalogue.getItems().addAll(items);
    }

    @FXML
    private void handleAddToCart() {
        CatalogueItem item = catalogue.getSelectionModel().getSelectedItem();
        if (item == null) {
            new Alert(Alert.AlertType.WARNING, "Select an item from the catalogue first.").showAndWait();
            return;
        }
        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Enter a valid number for quantity.").showAndWait();
            return;
        }
        if (quantity <= 0) {
            new Alert(Alert.AlertType.WARNING, "Quantity must be greater than 0.").showAndWait();
            return;
        }
        if (quantity > item.getAvailability()) {
            new Alert(Alert.AlertType.WARNING, "Not enough stock. Available: " + item.getAvailability()).showAndWait();
            return;
        }

        cartTable.getItems().add(new OrderItem(
                item.getItemId(),
                item.getDescription(),
                quantity,
                item.getPackageCost(),
                quantity * item.getPackageCost()
        ));
        updateTotal();
    }

    @FXML
    private void handleRemoveFromCart() {
        OrderItem item = cartTable.getSelectionModel().getSelectedItem();
        if (item == null) {return;}

        cartTable.getItems().remove(item);
        updateTotal();
    }

    @FXML
    private void handleSubmitOrder() {
        if (cartTable.getItems().isEmpty()) {return;}

        double total = 0;
        for (OrderItem item : cartTable.getItems()) {
            total += item.getAmount();
        }
        String merchantId = ipos.sa.UserSession.getInstance().getUsername();
        String orderdate = java.time.LocalDate.now().toString();

        int orderId = DatabaseManager.submitOrder(merchantId,orderdate,total, cartTable.getItems());
        //TODO: Error handle case where orderId -1 is returned (fail)
        cartTable.getItems().clear();
        updateTotal();
        loadCatalogue();
        loadOrders();
        loadBalance();

        javafx.scene.control.Alert ok = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        ok.setTitle("Order Placed");
        ok.setHeaderText("Order " + orderId + " submitted.");
        ok.setContentText(String.format("Total: £%.2f", total));
        ok.showAndWait();
    }

    private void updateTotal() {
        double total = 0;
        for(OrderItem item : cartTable.getItems()) {
            total += item.getAmount();
        }
        grandTotalLabel.setText(String.format("Grand Total: £%.2f", total));
    }

    private void loadOrders() {
        String merchantId = ipos.sa.UserSession.getInstance().getUsername();
        ordersTable.getItems().clear();
        ordersTable.getItems().addAll(DatabaseManager.getOrdersByMerchant(merchantId));
    }

    private void loadBalance() {
        String merchantId = ipos.sa.UserSession.getInstance().getUsername();
        double creditLimit = DatabaseManager.getCreditLimit(merchantId);
        double outstanding = DatabaseManager.getOutstandingBalance(merchantId);
        double available = creditLimit - outstanding;

        creditLimitLabel.setText(String.format("£%.2f", creditLimit));
        outstandingLabel.setText(String.format("£%.2f", outstanding));
        availableCreditLabel.setText(String.format("£%.2f", available));
        balanceLabel.setText(String.format("Balance: £%.2f / £%.2f", outstanding, creditLimit));

        invoicesTable.getItems().clear();
        invoicesTable.getItems().addAll(DatabaseManager.getUnpaidInvoices(merchantId));
    }



    @FXML
    private void handleMainMenu(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "main-menu.fxml", "IPOS Main Menu");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterCatalogue(String query) {
        catalogue.getItems().clear();
        List<CatalogueItem> items = DatabaseManager.getCatalogueItems();

        if(query == null || query.isEmpty()) {
            catalogue.getItems().addAll(items);
        }
        else {
            String l = query.toLowerCase();
            for(CatalogueItem item : items) {
                if(item.getDescription().toLowerCase().contains(l)
                        || String.valueOf(item.getItemId()).contains(l)
                        || item.getDescription().toLowerCase().contains(l)
                        || String.valueOf(item.getPackageCost()).contains(l)
                ) {catalogue.getItems().add(item);}
            }
        }
    }
}
