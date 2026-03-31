package ipos.sa;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneSwitcher {

    /**
     * Switches the current window to a new FXML scene.
     * @param event ActionEvent to track the source of the switch, JFX passes this in automatically
     * @param fxmlFile The name of the FXML file the Scene should load
     * @param title The title for the new window
     */
    public static void switchScene(ActionEvent event, String fxmlFile, String title) {
        try {
            // load the new FXML file
            FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlFile));
            Parent root = loader.load();

            // get the current stage from the ActionEvent
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // set and show the new scene
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading FXML: " + fxmlFile);
            e.printStackTrace();
        }
    }
}