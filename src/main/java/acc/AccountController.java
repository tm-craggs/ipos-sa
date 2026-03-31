package acc;

import db.DatabaseManager;
import ipos.sa.UserSession;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import ipos.sa.SceneSwitcher;

public class AccountController {

    // representing each field in the form
    @FXML private ChoiceBox<String> roleChoice;
    @FXML private VBox merchantFields;
    @FXML private TextField usernameField, limitField;
    @FXML private PasswordField passwordField;
    @FXML private RadioButton fixedRadio, flexibleRadio;
    @FXML private Button saveButton;

    // for later when application review is implemented
    @FXML private TableView<Object> pendingTable;
    @FXML private TableColumn<Object, String> colCompany, colReg, colEmail;

    @FXML
    public void initialize() {

        // set up items for the account type choice box
        roleChoice.setItems(FXCollections.observableArrayList("Merchant", "Manager", "Admin"));
        roleChoice.setValue("Merchant");

        // set up options that will only appear for merchant accounts
        merchantFields.visibleProperty().bind(roleChoice.valueProperty().isEqualTo("Merchant"));
        merchantFields.managedProperty().bind(merchantFields.visibleProperty());
        ToggleGroup discountGroup = new ToggleGroup();  // toggle group for discount plans
        fixedRadio.setToggleGroup(discountGroup);
        flexibleRadio.setToggleGroup(discountGroup);

        // disable main button while boxes are still empty
        saveButton.disableProperty().bind(
                usernameField.textProperty().isEmpty()
                        .or(passwordField.textProperty().isEmpty())
        );
    }

    /**
     * Handles the button click event for creating a new user.
     */
    @FXML
    private void handleCreateAccount() {

        // get values from role and username boxes
        String role = roleChoice.getValue();
        String user = usernameField.getText();
        String password = passwordField.getText(); // might need secure hashing later, depends on consultant

        System.out.println("Processing " + role + " creation for: " + user);

        // check if the account being created is a merchant, if yes, it needs the credit limit and discount plan vars
        if (role.equals("Merchant")) {

            // convert credit limit text to float
            float limit = Float.parseFloat(limitField.getText());

            // get the result of the discount plan button
            String discountPlan = null;
            if (fixedRadio.isSelected()) {
                discountPlan = "fixed";
            } else if (flexibleRadio.isSelected()) {
                discountPlan = "flexible";
            }

            // pass in base details, and the merchant-specific details
            DatabaseManager.addUser(user, password, role, limit, discountPlan);

        } else {
            // pass in base details only
            DatabaseManager.addUser(user, password, role);
        }

        // success feedback
        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Account Created");
        success.setHeaderText(null);
        success.setContentText(role + " account for " + user + " has been successfully created.");
        success.showAndWait();

        clearFields();
    }

    /**
     * Clears the username, password, and credit limit fields.
     */
    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        limitField.clear();
    }

    /**
     * Handles the back to Main Menu button click event.
     * @param event: The ActionEvent object representing the button click
     */
    @FXML
    private void handleBack(ActionEvent event) {
        // switch back to main menu
        SceneSwitcher.switchScene(event, "/ipos/sa/main-menu.fxml", "IPOS-SA - Main Menu");
    }

    /**
     * Handles the logout button click event.
     * @param event: The ActionEvent object representing the button click
     */
    @FXML
    private void handleLogout(ActionEvent event) {

        // confirm logout
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to logout?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        // if user confirms, logout UserSession and return to login screen
        if (confirm.getResult() == ButtonType.YES) {
            UserSession.logout();
            SceneSwitcher.switchScene(event, "/ipos/sa/login-window.fxml", "Login");
        }
    }

    // method to be completed later when team 33 API is avaliable for us to use
    @FXML
    private void handleReviewApp() {
        System.out.println("Reviewing selected application...");
        // code to move data to review goes here
    }
}