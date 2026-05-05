package chatapp.client;

import java.net.Socket;
import java.io.*;
import java.util.function.Consumer;

import chatapp.util.Config;

public class ChatClient {

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    // CONNECT + LOGIN
    public boolean connect(String email, String password) {

        try {
            socket = new Socket("localhost", Config.PORT);

            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            // send login
            output.writeUTF(email);
            output.writeUTF(password);

            String response = input.readUTF();

            return response.equals("SUCCESS");

        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
            return false;
        }
    }

    // SEND MESSAGE
    public void sendMessage(String message) {
        try {
            output.writeUTF(message);
        } catch (IOException e) {
            System.out.println("Send error: " + e.getMessage());
        }
    }
    
    public void sendFile(File file, String receiver) {
        try {
            // 1. send header WITH receiver
            output.writeUTF("FILE:TO:" + receiver + ":" + file.getName());

            // 2. send file size
            long fileSize = file.length();
            output.writeLong(fileSize);

            // 3. send file data
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[4096];

            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            fis.close();

            System.out.println("File sent: " + file.getName());

        } catch (IOException e) {
            System.out.println("File send error: " + e.getMessage());
        }
    }

    // LISTEN FOR MESSAGES (callback to UI)
    public void listenForMessages(Consumer<String> onMessageReceived) {

        new Thread(() -> {
            try {
                while (true) {
                     String msg = input.readUTF();

                    if (msg.contains("FILE:")) {

                        // 1. read file size
                        long fileSize = input.readLong();

                        // 2. extract filename
                        String fileName = msg.substring(msg.indexOf("FILE:") + 5).trim();

                        // save as received_filename
                        FileOutputStream fos = new FileOutputStream("received_" + fileName);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        long remaining = fileSize;

                        // 3. read file data
                        while (remaining > 0) {
                            bytesRead = input.read(buffer, 0, (int)Math.min(buffer.length, remaining));
                            fos.write(buffer, 0, bytesRead);
                            remaining -= bytesRead;
                        }

                        fos.close();

                        String senderPart = msg.substring(0, msg.indexOf("FILE:"));
                        String cleanFileName = msg.substring(msg.indexOf("FILE:") + 5);

                        onMessageReceived.accept("📥 " + senderPart + "sent file: " + cleanFileName);

                    } else {
                        onMessageReceived.accept(msg);
                    }
                }
            } catch (IOException e) {
                System.out.println("Disconnected from server.");
            }
        }).start();
    }
}