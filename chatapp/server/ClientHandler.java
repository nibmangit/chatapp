package chatapp.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;

import chatapp.database.*;

public class ClientHandler implements Runnable {

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private String email;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            System.out.println("New client handler started: " + socket.getInetAddress());
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            if (!authenticate()) return;

            while (true) {
                String message = input.readUTF();

                if (message.startsWith("FILE:")) {
                    handleFile(message);
                } else {
                    handleText(message);
                }
            }

        } catch (IOException e) {
            handleDisconnect();
        }
    } 
    
    private boolean authenticate() throws IOException {
        email = input.readUTF();
        String password = input.readUTF();

        boolean valid = UserDAO.validateLogin(email, password);

        if (valid) {
            output.writeUTF("SUCCESS");
            System.out.println(email + " logged in");

            Server.clients.put(email, output);
            Server.sendActiveUsers();
            return true;
        } else {
            output.writeUTF("FAIL");
            socket.close();
            return false;
        }
    }
    
    private void handleText(String message) throws IOException {

        if (!message.startsWith("TO:")) return;

        String[] parts = message.split(":", 3);

        String receiver = parts[1];
        String actualMessage = parts[2];

        String time = LocalDateTime.now().toString().substring(11, 16);
        String type = receiver.equals("ALL") ? "(public)" : "(private)";
        String formattedMsg = email + type + " [" + time + "]: " + actualMessage; 

        System.out.println(formattedMsg);

        MessageDAO.saveMessage(email, receiver, actualMessage);

        if (receiver.equals("ALL")) {
            Server.broadcast(formattedMsg);
        } else {
            DataOutputStream target = Server.clients.get(receiver);
            DataOutputStream sender = Server.clients.get(email);

            if (target != null) target.writeUTF(formattedMsg);
            if (sender != null) sender.writeUTF(formattedMsg);
        }
    }
    
    private void handleFile(String message) throws IOException {

        long fileSize = input.readLong();

        byte[] buffer = new byte[4096];
        int bytesRead;
        long remaining = fileSize;

        String[] parts = message.split(":", 4);
        String receiver = parts[2];
        String fileName = parts[3];

        String type = receiver.equals("ALL") ? "(public)" : "(private)";
        String time = LocalDateTime.now().toString().substring(11, 16);
        String header = email + type +" [" + time + "]: FILE:" + fileName;

        FileDAO.saveFile(email, receiver, fileName, fileSize);

        if (receiver.equals("ALL")) {

            for (DataOutputStream client : Server.clients.values()) {
                client.writeUTF(header);
                client.writeLong(fileSize);
            }

            while (remaining > 0) {
                bytesRead = input.read(buffer, 0, (int)Math.min(buffer.length, remaining));

                for (DataOutputStream client : Server.clients.values()) {
                    client.write(buffer, 0, bytesRead);
                }

                remaining -= bytesRead;
            }

        } else {

            DataOutputStream target = Server.clients.get(receiver);
            DataOutputStream sender = Server.clients.get(email);

            if (target != null) {
                target.writeUTF(header);
                target.writeLong(fileSize);
            }

            if (sender != null) {
                sender.writeUTF(header);
                sender.writeLong(fileSize);
            }

            while (remaining > 0) {
                bytesRead = input.read(buffer, 0, (int)Math.min(buffer.length, remaining));

                if (target != null) target.write(buffer, 0, bytesRead);
                if (sender != null) sender.write(buffer, 0, bytesRead);

                remaining -= bytesRead;
            }
        }

        System.out.println("File handled: " + fileName);
    }
    
    private void handleDisconnect() {
        System.out.println("Client disconnected: " + socket.getInetAddress());

        if (email != null) {
            Server.clients.remove(email);
            Server.sendActiveUsers();
        }
    }
    
}