package org.example.clientsevermsgexample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerView {

    @FXML
    private TextField tf_message;

    @FXML
    private VBox vbox_messages;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataOutputStream dos;

    public ServerView() {
        try {
            // Start the server
            serverSocket = new ServerSocket(6666);
            updateChatView("Server is running and waiting for clients...");

            // Accept client connection
            new Thread(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    updateChatView("Client connected!");

                    dos = new DataOutputStream(clientSocket.getOutputStream());

                    // Start a thread to listen for incoming messages
                    new Thread(() -> {
                        try (var dis = new java.io.DataInputStream(clientSocket.getInputStream())) {
                            String message;
                            while ((message = dis.readUTF()) != null) {
                                updateChatView("Client: " + message);
                            }
                        } catch (Exception e) {
                            updateChatView("Connection closed: " + e.getMessage());
                        }
                    }).start();
                } catch (Exception e) {
                    updateChatView("Error accepting client: " + e.getMessage());
                }
            }).start();
        } catch (Exception e) {
            updateChatView("Error starting server: " + e.getMessage());
        }
    }

    @FXML
    public void sendMessage(ActionEvent actionEvent) {
        String message = tf_message.getText();
        if (message.isEmpty()) return;

        try {
            dos.writeUTF(message); // Send message to client
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
