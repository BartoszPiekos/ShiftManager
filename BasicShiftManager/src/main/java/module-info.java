module com.example.basicshiftmanager {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.basicshiftmanager to javafx.fxml;
    exports com.example.basicshiftmanager;
}