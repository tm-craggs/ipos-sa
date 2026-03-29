module ipos.sa {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens ipos.sa to javafx.fxml;
    opens cat to javafx.fxml, javafx.base;
    exports ipos.sa;

    opens acc to javafx.fxml;
    exports acc;

}