package chatapp.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FileDAO {

    public static void saveFile(String senderEmail, String receiverEmail, String fileName, long fileSize) {

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "INSERT INTO files (sender_email, receiver_email, file_name, file_size) VALUES (?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, senderEmail);
            stmt.setString(2, receiverEmail);
            stmt.setString(3, fileName);
            stmt.setLong(4, fileSize);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("File save error: " + e.getMessage());
        }
    }
}