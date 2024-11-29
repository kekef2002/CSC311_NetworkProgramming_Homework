package org.example.clientsevermsgexample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.DataOutputStream;
import java.net.Socket;

public class ClientView {

    @FXML
    private TextField tf_message;

    @FXML
    private VBox vbox_messages;

    private Socket socket;
    private DataOutputStream dos;

    public ClientView() {
        try {
            // Connect to the server
            socket = new Socket("localhost", 6666);
            dos = new DataOutputStream(socket.getOutputStream());

            // Start a thread to listen for incoming messages
            new Thread(() -> {
                try (var dis = new java.io.DataInputStream(socket.getInputStream())) {
                    String message;
                    while ((message = dis.readUTF()) != null) {
                        updateChatView("Server: " + message);
                    }
                } catch (Exception e) {
                    updateChatView("Connection closed: " + e.getMessage());
                }
            }).start();
        } catch (Exception e) {
            updateChatView("Error connecting to server: " + e.getMessage());
        }
    }

    @FXML
    public void sendMessage(ActionEvent actionEvent) {
        String message = tf_message.getText();
        if (message.isEmpty()) return;

        try {
            dos.writeUTF(message); // Send message to server
            updateChatView("You: " + message); // Display message in chat view
            tf_message.clear(); // Clear input field
        } catch (Exception e) {
            updateChatView("Error sending message: " + e.getMessage());
        }
    }

    private void updateChatView(String message) {
        Platform.runLater(() -> vbox_messages.getChildren().add(new Text(message)));
    }
}
