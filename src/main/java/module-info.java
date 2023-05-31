module com.example.finalerhis {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.finalerhis to javafx.fxml;
    exports com.example.finalerhis;
}