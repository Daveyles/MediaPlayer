module com.example.tefosmediaplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.example.tefosmediaplayer to javafx.fxml;
    exports com.example.tefosmediaplayer;
}