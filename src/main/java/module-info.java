module ipos.sa {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens ipos.sa to javafx.fxml;
    opens cat to javafx.fxml;
    exports ipos.sa;
}