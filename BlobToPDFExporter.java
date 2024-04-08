import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BlobToPDFExporter {

    private static final String DB_URL = "jdbc:db2://localhost:50000/yourdatabase"; // Change to your database URL
    private static final String USER = "yourusername"; // Change to your database username
    private static final String PASS = "yourpassword"; // Change to your database password
    private static final String SELECT_QUERY = "SELECT pdf_content FROM documents WHERE id = ?"; // Adjust based on your table structure

    public static void main(String[] args) {
        // Ensure the driver is registered
        try {
            Class.forName("com.ibm.db2.jcc.DB2Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Document ID to retrieve
        int documentId = 1; // Change this to the document ID you want to retrieve

        // PDF file to save as
        String outputFile = "output.pdf"; // Change to your desired output file path

        // Read and export the BLOB
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_QUERY)) {
            
            preparedStatement.setInt(1, documentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Blob blob = resultSet.getBlob("pdf_content");
                    byte[] buffer = blob.getBytes(1, (int) blob.length());
                    writeBytesToFile(outputFile, buffer);
                    System.out.println("PDF exported successfully to " + outputFile);
                } else {
                    System.out.println("Document not found with ID: " + documentId);
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeBytesToFile(String filePath, byte[] bytes) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(bytes);
        }
    }
}
