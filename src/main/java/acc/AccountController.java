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
    @FXML private TableColumn<UserAccount, String> colUser, colType, colStatus;

    @FXML
    public void initialize() {

        String callerRole = UserSession.getInstance().getType();

        List<String> roles = switch (callerRole) {
            case "Director", "Admin" -> List.of("Merchant", "Manager", "Admin");
            case "Manager"  -> List.of("Merchant", "Manager");
            default         -> List.of();
        };
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
     * Returns a numeric rank for a given role, used to enforce the permission hierarchy.
     * Higher number = higher rank.
     */
    private int getRank(String role) {
        return switch (role) {
            case "Merchant" -> 0;
            case "Manager"  -> 1;
            case "Admin"    -> 2;
            case "Director" -> 3;
            default         -> -1;
        };
    }


    @FXML
    private void handleEditAccount() {
        UserAccount selected = accountsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No selection", "Please select an account to edit.");
            return;
        }

        String callerRole = UserSession.getInstance().getType();
        boolean isMerchant = selected.getType().equals("Merchant");
        boolean isDirector = callerRole.equals("Director");

        if (getRank(callerRole) <= getRank(selected.getType())) {
            showWarning("Permission Denied", "You can only edit accounts below your role level.");
            return;
        }

        if (selected.getStatus() != null && selected.getStatus().equals("In-Default") && !isDirector) {
            showWarning("Account In Default", "This account is in default and can only be modified by the Director.");
            return;
        }

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
        roleBox.setValue(selected.getType());
        List<String> roleOptions = switch (callerRole) {
            case "Director", "Admin" -> List.of("Merchant", "Manager", "Admin");
            case "Manager"  -> List.of("Merchant", "Manager");
            default         -> List.of();
        };
        roleBox.setItems(FXCollections.observableArrayList(roleOptions));
        grid.add(new Label("Role:"), 0, row);
        grid.add(roleBox, 1, row++);

        Label statusLabel = new Label("Status:");
        ChoiceBox<String> statusBox = new ChoiceBox<>();
        if (selected.getStatus() != null && selected.getStatus().equals("Default")) {
            if (isDirector) {
                statusBox.setItems(FXCollections.observableArrayList("Normal", "Suspended", "In-Default"));
            } else {
                statusBox.setItems(FXCollections.observableArrayList("Default"));
                statusBox.setDisable(true);
            }
        } else {
            statusBox.setItems(FXCollections.observableArrayList("Normal", "Suspended", "In-Default"));
        }
        statusBox.setValue(selected.getStatus() != null ? selected.getStatus() : "Normal");
        grid.add(statusLabel, 0, row);
        grid.add(statusBox, 1, row++);

        Label limitLabel = new Label("Credit Limit (£):");
        TextField merchantLimitField = new TextField();
        if (isMerchant) {
            double currentLimit = DatabaseManager.getCreditLimit(selected.getUsername());
            merchantLimitField.setText(String.valueOf(currentLimit));
        }
        grid.add(limitLabel, 0, row);
        grid.add(merchantLimitField, 1, row++);

        dialog.getDialogPane().lookupButton(confirmButton).disableProperty().bind(
                roleBox.valueProperty().isEqualTo("Merchant")
                        .and(merchantLimitField.textProperty().isEmpty())
        );

        Label planLabel = new Label("Discount Plan:");
        RadioButton fixedOption    = new RadioButton("Fixed");
        RadioButton flexibleOption = new RadioButton("Flexible");
        ToggleGroup planGroup      = new ToggleGroup();
        fixedOption.setToggleGroup(planGroup);
        flexibleOption.setToggleGroup(planGroup);
        javafx.scene.layout.HBox planBox = new javafx.scene.layout.HBox(15, fixedOption, flexibleOption);

        if (isMerchant) {
            String currentPlan = DatabaseManager.getDiscountPlan(selected.getUsername());
            if ("flexible".equals(currentPlan)) {
                flexibleOption.setSelected(true);
            } else {
                fixedOption.setSelected(true);
            }
        } else {
            fixedOption.setSelected(true);
        }
        grid.add(planLabel, 0, row);
        grid.add(planBox, 1, row++);

        if (selected.getStatus() != null && selected.getStatus().equals("In-Default")) {
            roleBox.setDisable(true);
            merchantLimitField.setDisable(true);
            fixedOption.setDisable(true);
            flexibleOption.setDisable(true);
        }

        Runnable updateMerchantFieldVisibility = () -> {
            boolean showMerchantFields = roleBox.getValue().equals("Merchant");

            statusLabel.setVisible(showMerchantFields);
            statusLabel.setManaged(showMerchantFields);
            statusBox.setVisible(showMerchantFields);
            statusBox.setManaged(showMerchantFields);

            limitLabel.setVisible(showMerchantFields);
            limitLabel.setManaged(showMerchantFields);
            merchantLimitField.setVisible(showMerchantFields);
            merchantLimitField.setManaged(showMerchantFields);

            planLabel.setVisible(showMerchantFields);
            planLabel.setManaged(showMerchantFields);
            planBox.setVisible(showMerchantFields);
            planBox.setManaged(showMerchantFields);

            dialog.getDialogPane().getScene().getWindow().sizeToScene();
        };

        updateMerchantFieldVisibility.run();

        roleBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.equals("Merchant") && !oldVal.equals("Merchant")) {
                statusBox.setValue("Normal");
                merchantLimitField.clear();
                fixedOption.setSelected(true);
            }
            updateMerchantFieldVisibility.run();
        });

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == confirmButton) {

                boolean selectedAsMerchant = roleBox.getValue().equals("Merchant");

                // Apply role change if it has changed
                if (!roleBox.getValue().equals(selected.getType())) {
                    DatabaseManager.updateUserType(selected.getId(), roleBox.getValue());

                    // If promoted away from Merchant, clear merchant-specific fields and stop
                    if (isMerchant && !selectedAsMerchant) {
                        DatabaseManager.clearMerchantFields(selected.getId());
                        loadAccountsTable();
                        return;
                    }
                }

                if (selectedAsMerchant) {
                    DatabaseManager.setUserStatus(selected.getId(), statusBox.getValue());
                    try {
                        float newLimit = Float.parseFloat(merchantLimitField.getText().trim());
                        DatabaseManager.updateCreditLimit(selected.getId(), newLimit);
                    } catch (NumberFormatException e) {
                        showWarning("Invalid Input", "Credit limit must be a number.");
                        return;
                    }
                    String chosenPlan = flexibleOption.isSelected() ? "flexible" : "fixed";
                    DatabaseManager.updateDiscountPlan(selected.getId(), chosenPlan);
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

        String callerRole = UserSession.getInstance().getType();

        if (getRank(callerRole) <= getRank(selected.getType())) {
            showWarning("Permission Denied", "You can only delete accounts below your role level.");
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