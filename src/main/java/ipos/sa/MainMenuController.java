package ipos.sa;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;


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
            closeWindow();
            System.out.println("Logging out...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-window.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("IPOS");

            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void handleAcc() {
        System.out.println("Opening IPOS-ACC...");
    }

    @FXML
    private void handleCat() {
        System.out.println("Opening IPOS-CAT...");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ipos-sa-cat/cat-stcokmanager.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Catalogue");
            stage.setScene(new Scene(root));
            stage.show();
            closeWindow();
        } catch (Exception e) {e.printStackTrace();}
    }

    @FXML
    private void handleOrd() {
        System.out.println("Opening IPOS-ORD...");
    }

    @FXML
    private void handleRpt() {
        System.out.println("Opening IPOS-RPT...");
    }

    private void closeWindow() {
        Stage stage = (Stage) loggedInUserLabel.getScene().getWindow();
        stage.close();
    }
}