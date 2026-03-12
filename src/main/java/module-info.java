module com.ipos.sa {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens ipos.sa to javafx.fxml;
    exports ipos.sa;
}