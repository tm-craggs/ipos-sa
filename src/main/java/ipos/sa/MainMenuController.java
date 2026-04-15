package ipos.sa;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.stage.Stage;


public class MainMenuController {

    @FXML private Label loggedInUserLabel;
    @FXML private Button accButton;
    @FXML private Button catButton;
    @FXML private Button ordButton;
    @FXML private Button rptButton;

    @FXML
    public void initialize() {
        try{
            String name = UserSession.getInstance().getUsername();
            loggedInUserLabel.setText(name);

            applyPermissions(UserSession.getInstance().getType());

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void applyPermissions(String type){

        if (type == null) return;
        type = type.toLowerCase();

        switch (type) {

            case "director":
            case "admin":
                showButton(accButton);
                showButton(catButton);
                showButton(ordButton);
                showButton(rptButton);
                break;

            case "merchant":
                showButton(catButton);
                showButton(ordButton);
                break;

            case "manager":
                showButton(rptButton);
                showButton(accButton);

            default:
                System.out.println("Unrecognised role, no subsystems activated");
        }


    }

    private void showButton(Button btn) {
        btn.setVisible(true);
        btn.setManaged(true);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            UserSession.logout();
            System.out.println("Logging out...");

            SceneSwitcher.switchScene(event, "login-window.fxml", "Login");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void handleAcc(ActionEvent event) {

        System.out.println("Opening IPOS-ACC...");
        SceneSwitcher.switchScene(event, "/acc/acc-window.fxml", "IPOS-ACC");

    }

    @FXML
    private void handleCat(ActionEvent event) {
        System.out.println("Opening IPOS-CAT...");
        try {
            SceneSwitcher.switchScene(event, "/cat/cat-stockmanager.fxml", "IPOS-CAT");
        } catch (Exception e) {e.printStackTrace();}
    }

    @FXML
    private void handleOrd(ActionEvent event) {
        System.out.println("Opening IPOS-ORD...");
        try {
            SceneSwitcher.switchScene(event, "/ord/ORD.fxml", "IPOS-ORD");
        } catch (Exception e) {e.printStackTrace();}
    }

    @FXML
    private void handleRpt(ActionEvent event) {
        System.out.println("Opening IPOS-RPT...");
        SceneSwitcher.switchScene(event, "/rpt/report-window.fxml", "IPOS-RPT");
    }
    private void closeWindow() {
        Stage stage = (Stage) loggedInUserLabel.getScene().getWindow();
        stage.close();
    }
    
}