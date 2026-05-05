package chatapp.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MessageDAO {

    public static void saveMessage(String senderEmail, String receiverEmail, String message) {

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "INSERT INTO messages (sender_email, receiver_email, message) VALUES (?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, senderEmail);
            stmt.setString(2, receiverEmail);
            stmt.setString(3, message);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Save message error: " + e.getMessage());
        }
    }
}