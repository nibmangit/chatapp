package chatapp.ui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;

import chatapp.database.UserDAO;

public class RegisterView {

    public Scene getScene() {

        Label title = new Label("Register");

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.RED);

        Button registerBtn = new Button("Register");
        Button goToLoginBtn = new Button("Go to Login");

        registerBtn.setOnAction(e -> {

            String fullName = fullNameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            
            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("All Fields required.");
                return;
            }
            
            if (!email.contains("@") || !email.contains(".")) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("Invalid email format."); 
                return;
            }

            boolean success = UserDAO.registerUser(fullName, email, password);

            if (success) {
                messageLabel.setTextFill(Color.GREEN);
                messageLabel.setText("Registration successful, Go to login.");
            } else {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("Registration failed.");
            }
        });

        goToLoginBtn.setOnAction(e -> {
            MainApp.showLogin();
        });

        HBox buttonBox = new HBox(20);
        buttonBox.getChildren().addAll(registerBtn, goToLoginBtn);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        layout.getChildren().addAll(
                title,
                fullNameField,
                emailField,
                passwordField,
                messageLabel,
                buttonBox
        );

        return new Scene(layout, 300, 250);
    }
}