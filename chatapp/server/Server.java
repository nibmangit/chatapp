package chatapp.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.io.DataOutputStream;

import chatapp.util.Config;

public class Server {
    public static Map<String, DataOutputStream> clients = new HashMap<>();

    public static void main(String[] args) {

        System.out.println("Server is starting...");

        try {
            ServerSocket serverSocket = new ServerSocket(Config.PORT);
            System.out.println("Server started on port " + Config.PORT);
            System.out.println("Waiting for clients...");

            while (true) {
                // Accept client
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress());

                // Create new handler for each client
                ClientHandler handler = new ClientHandler(socket);

                // Start new thread
                Thread thread = new Thread(handler);
                thread.start();
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
    
        public static void broadcast(String message) {

        for (DataOutputStream client : clients.values()) {
            try {
                client.writeUTF(message);
            } catch (Exception e) {
                System.out.println("Error sending message");
            }
        }
    }
        
        public static void sendActiveUsers() {

            StringBuilder userList = new StringBuilder("USERLIST:");

            for (String email : clients.keySet()) {
                userList.append(email).append(",");
            }

            for (DataOutputStream client : clients.values()) {
                try {
                    client.writeUTF(userList.toString());
                } catch (Exception e) {
                    System.out.println("Error sending user list");
                }
            }
        }
}