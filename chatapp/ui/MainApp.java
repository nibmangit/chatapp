package chatapp.ui;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage window;

    @Override
    public void start(Stage primaryStage) {

        window = primaryStage;

        showLogin(); 
    }

    public static void showRegister() {
        RegisterView registerView = new RegisterView();

        window.setTitle("Register");
        window.setScene(registerView.getScene());
        window.show();
    }

    public static void showLogin() {
        LoginView loginView = new LoginView();

        window.setTitle("Login");
        window.setScene(loginView.getScene());
        window.show();
    }
    
    public static void showChat(String email, String password) {
        ChatView chatView = new ChatView(email, password);

        window.setTitle("Chat-Area(" + email+")");
        window.setScene(chatView.getScene());
        window.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}