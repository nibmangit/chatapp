package chatapp.ui;

import javafx.application.Platform;
import javafx.stage.FileChooser;
import java.io.File;

import javafx.scene.Scene;
import javafx.scene.control.*; 
import javafx.scene.layout.*;
import javafx.geometry.Insets; 

import chatapp.client.ChatClient;

public class ChatView {
    
    private String email;
    private String password;

    public ChatView(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Scene getScene() {
        
        ListView<String> userList = new ListView<>();
        userList.setPrefWidth(120); 

        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);//Full screen

        TextField messageField = new TextField();
        messageField.setPromptText("Type your message...");

        Button sendBtn = new Button("Send");
        Button fileBtn = new Button("Attach");
        
        ChatClient client = new ChatClient();
        boolean connected = client.connect(email, password);

        if (!connected) {
            chatArea.appendText("Connection failed (check server or login)\n");
        } else {
            client.listenForMessages(msg -> {
                 Platform.runLater(() -> {

                    if (msg.startsWith("USERLIST:")) {

                        String users = msg.substring(9);
                        String[] userArray = users.split(",");

                        userList.getItems().clear();
                        userList.getItems().add("ALL");

                        for (String user : userArray) {
                            if (!user.isEmpty()) {
                                userList.getItems().add(user);
                            }
                        }
                        userList.getSelectionModel().select("ALL");

                    } else if (msg.contains("FILE:")) {
                        String clean = msg.substring(msg.indexOf("FILE:") + 5);
                        chatArea.appendText("📎 File: " + clean + "\n");

                    } else {
                        chatArea.appendText(msg + "\n");
                    }
                });
            });
        }
        
        sendBtn.setOnAction(e -> {
            
            String text = messageField.getText().trim();

            if (text.isEmpty()) {
                return; // prevent empty message
            }
            String receiver = userList.getSelectionModel().getSelectedItem();

            if (receiver == null) {
                receiver = "ALL";
            }

            String message = "TO:" + receiver + ":" + text;

            client.sendMessage(message);
            messageField.clear();
        });
        
        fileBtn.setOnAction(e -> {

            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);

            if (file != null) { 
                String selected = userList.getSelectionModel().getSelectedItem();

                final String receiver;
                if (selected == null) {
                    receiver = "ALL";
                } else {
                    receiver = selected;
                }

                new Thread(() -> {
                    client.sendFile(file, receiver);
                }).start();
            }
        });
        BorderPane root = new BorderPane();
        root.setCenter(chatArea);
        root.setRight(userList);

        HBox inputBox = new HBox(10);
        messageField.setPrefWidth(200);
        sendBtn.setPrefWidth(80);
        fileBtn.setPrefWidth(80);
        inputBox.getChildren().addAll(messageField, fileBtn, sendBtn);
        inputBox.setPadding(new Insets(10));
        
        root.setBottom(inputBox);

        return new Scene(root, 400, 400);
    }
}