module com.ipos.sa {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.ipos.sa to javafx.fxml;
    exports com.ipos.sa;
}