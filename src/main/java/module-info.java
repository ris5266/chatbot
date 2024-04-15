module com.example.chatbot {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires org.json;


    opens com.example.chatbot to javafx.fxml;
    exports com.example.chatbot;
}