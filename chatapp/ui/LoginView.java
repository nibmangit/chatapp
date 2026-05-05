package chatapp.ui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;

import chatapp.database.UserDAO;

public class LoginView {

    public Scene getScene() {

        Label title = new Label("Login");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.RED);

        Button loginBtn = new Button("Login");
        Button goToRegisterBtn = new Button("Go to Register");

        HBox buttonBox = new HBox(20);
        buttonBox.getChildren().addAll(loginBtn, goToRegisterBtn);

        goToRegisterBtn.setOnAction(e -> {
            MainApp.showRegister();
        });

        loginBtn.setOnAction(e -> {

            String email = emailField.getText();
            String password = passwordField.getText();

            boolean valid = UserDAO.validateLogin(email, password);

            if (valid) {
                messageLabel.setTextFill(Color.GREEN);
                messageLabel.setText("Login successful");

                MainApp.showChat(email, password);
            } else {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("Invalid email or password");
            }
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        layout.getChildren().addAll(
                title,
                emailField,
                passwordField,
                messageLabel,
                buttonBox
        );

        return new Scene(layout, 300, 220);
    }
}