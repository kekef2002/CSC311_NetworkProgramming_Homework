module org.example.clientsevermsgexample {
    requires javafx.controls;
    requires javafx.fxml;



    opens org.example.clientsevermsgexample to javafx.fxml;

    exports org.example.clientsevermsgexample;

}