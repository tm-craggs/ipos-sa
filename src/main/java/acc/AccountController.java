package acc;

import db.DatabaseManager;
import ipos.sa.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ipos.sa.SceneSwitcher;
import java.util.List;

public class AccountController {

    @FXML private ChoiceBox<String> roleChoice;
    @FXML private VBox merchantFields;
    @FXML private TextField usernameField, limitField;
    @FXML private PasswordField passwordField;
    @FXML private RadioButton fixedRadio, flexibleRadio;
    @FXML private Button saveButton;

    @FXML private TableView<Object> pendingTable;
    @FXML private TableColumn<Object, String> colCompany, colReg, colEmail;

    @FXML private TableView<UserAccount> accountsTable;
    @FXML private TableColumn<UserAccount, Integer> colId;
    @FXML private TableColumn<UserAccount, String>  colUser, colType, colStatus;

    @FXML
    public void initialize() {

        String callerRole = UserSession.getInstance().getType();
        List<String> roles = callerRole.equals("Director")
                ? List.of("Merchant", "Manager", "Admin")
                : List.of("Merchant", "Manager");
        roleChoice.setItems(FXCollections.observableArrayList(roles));
        roleChoice.setValue("Merchant");

        merchantFields.visibleProperty().bind(roleChoice.valueProperty().isEqualTo("Merchant"));
        merchantFields.managedProperty().bind(merchantFields.visibleProperty());

        ToggleGroup discountGroup = new ToggleGroup();
        fixedRadio.setToggleGroup(discountGroup);
        flexibleRadio.setToggleGroup(discountGroup);

        saveButton.disableProperty().bind(
                usernameField.textProperty().isEmpty()
                        .or(passwordField.textProperty().isEmpty())
        );

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadAccountsTable();
    }

    private void loadAccountsTable() {
        String callerRole = UserSession.getInstance().getType();
        ObservableList<UserAccount> data = FXCollections.observableArrayList(
                DatabaseManager.getUsers(callerRole)
        );
        accountsTable.setItems(data);
    }


    /**
     * Here is a quick overview of what permissions each role has:
     * Merchant: Cannot access this subsystem
     * Manager: Can perform CRUD operations on merchant accounts apart from restoring an account from in-default
     * cannot see other managers, admins, or director.
     * Admin: Same permissions as manager, but can see and perform CRUD operations on manager accounts.
     * Director: Can see and perform CRUD operations on all accounts and can restore merchants from in-default.
     */
    @FXML
    private void handleEditAccount() {
        UserAccount selected = accountsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No selection", "Please select an account to edit.");
            return;
        }

        String callerRole = UserSession.getInstance().getType();
        boolean isMerchant = selected.getType().equals("Merchant");

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Account");
        dialog.setHeaderText("Editing: " + selected.getUsername());

        ButtonType confirmButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);

        int row = 0;

        ChoiceBox<String> roleBox = new ChoiceBox<>();
        if (callerRole.equals("Director")) {
            roleBox.setItems(FXCollections.observableArrayList("Merchant", "Manager", "Admin"));
        } else if (callerRole.equals("Admin")) {
            roleBox.setItems(FXCollections.observableArrayList("Merchant", "Manager"));
        }
        if (!callerRole.equals("Manager")) {
            roleBox.setValue(selected.getType());
            grid.add(new Label("Role:"), 0, row);
            grid.add(roleBox, 1, row++);
        }

        ChoiceBox<String> statusBox = new ChoiceBox<>();
        if (callerRole.equals("Director")) {
            statusBox.setItems(FXCollections.observableArrayList("Normal", "Suspended", "Default"));
        } else {
            if (selected.getStatus().equals("Default")) {
                statusBox.setItems(FXCollections.observableArrayList("Default"));
                statusBox.setDisable(true);
            } else if (callerRole.equals("Manager")) {
                statusBox.setItems(FXCollections.observableArrayList("Normal", "Suspended", "Default"));
            } else {
                statusBox.setItems(FXCollections.observableArrayList("Normal", "Suspended"));
            }
        }
        statusBox.setValue(selected.getStatus());
        grid.add(new Label("Status:"), 0, row);
        grid.add(statusBox, 1, row++);

        TextField limitField = new TextField();
        ChoiceBox<String> planBox = new ChoiceBox<>();

        if (isMerchant) {
            double currentLimit = DatabaseManager.getCreditLimit(selected.getUsername());
            String currentPlan  = DatabaseManager.getDiscountPlan(selected.getUsername());

            limitField.setText(String.valueOf(currentLimit));
            grid.add(new Label("Credit Limit (£):"), 0, row);
            grid.add(limitField, 1, row++);

            planBox.setItems(FXCollections.observableArrayList("fixed", "flexible"));
            planBox.setValue(currentPlan);
            grid.add(new Label("Discount Plan:"), 0, row);
            grid.add(planBox, 1, row++);
        }

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == confirmButton) {

                if (!callerRole.equals("Manager") && roleBox.getValue() != null) {
                    if (!roleBox.getValue().equals(selected.getType())) {
                        DatabaseManager.updateUserType(selected.getId(), roleBox.getValue());
                    }


                    DatabaseManager.setUserStatus(selected.getId(), statusBox.getValue());
                }

                if (isMerchant) {
                    try {
                        float newLimit = Float.parseFloat(limitField.getText().trim());
                        DatabaseManager.updateCreditLimit(selected.getId(), newLimit);
                    } catch (NumberFormatException e) {
                        showWarning("Invalid Input", "Credit limit must be a number.");
                        return;
                    }
                    DatabaseManager.updateDiscountPlan(selected.getId(), planBox.getValue());
                }

                loadAccountsTable();
            }
        });
    }

    @FXML
    private void handleDeleteAccount() {
        UserAccount selected = accountsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No selection", "Please select an account to delete.");
            return;
        }

        if (!UserSession.getInstance().getType().equals("Director") && selected.getType().equals("Admin")) {
            showWarning("Permission denied", "Only the Director can delete Admin accounts.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete account '" + selected.getUsername() + "'? This cannot be undone.",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            DatabaseManager.deleteUser(selected.getId());
            loadAccountsTable();
        }
    }

    @FXML
    private void handleCreateAccount() {
        String role     = roleChoice.getValue();
        String user     = usernameField.getText();
        String password = passwordField.getText();

        if (DatabaseManager.usernameExists(user)) {
            showWarning("Error", "Username is taken, please choose a different username.");
            clearFields();
            return;
        }

        System.out.println("Processing " + role + " creation for: " + user);

        if (role.equals("Merchant")) {
            float  limit        = Float.parseFloat(limitField.getText());
            String discountPlan = fixedRadio.isSelected() ? "fixed" : "flexible";
            DatabaseManager.addUser(user, password, role, limit, discountPlan);
        } else {
            DatabaseManager.addUser(user, password, role);
        }

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Account Created");
        success.setHeaderText(null);
        success.setContentText(role + " account for " + user + " has been successfully created.");
        success.showAndWait();

        clearFields();
        loadAccountsTable();
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        limitField.clear();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneSwitcher.switchScene(event, "/ipos/sa/main-menu.fxml", "IPOS-SA - Main Menu");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to logout?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            UserSession.logout();
            SceneSwitcher.switchScene(event, "/ipos/sa/login-window.fxml", "Login");
        }
    }

    @FXML
    private void handleReviewApp() {
        System.out.println("Reviewing selected application...");
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}