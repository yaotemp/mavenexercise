import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClobInsertExample {

    private static final String JDBC_URL = "jdbc:db2://yourdb2server:50000/yourdatabase";
    private static final String JDBC_USER = "yourusername";
    private static final String JDBC_PASSWORD = "yourpassword";

    public static void main(String[] args) {
        // Path to the file containing the CLOB data
        String filePath = "/path/to/your/clobdatafile.txt";
        long retUnqKey = 12345L; // Example key, replace with actual value

        try {
            Path path = Paths.get(filePath);
            long responseLength = Files.size(path);
            
            try (FileReader fileReader = new FileReader(filePath)) {
                // Call insertTest to insert the CLOB data
                insertTest(retUnqKey, fileReader, responseLength);
                System.out.println("CLOB data inserted successfully!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void insertTest(long retUnqKey, Reader isResponse, long responseLength) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Step 1: Establish a database connection
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);

            // Step 2: Prepare the SQL statement
            String sql = "INSERT INTO yourtable (key_column, clob_column) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(sql);

            // Step 3: Set the key and CLOB data in the PreparedStatement
            preparedStatement.setLong(1, retUnqKey);
            preparedStatement.setCharacterStream(2, isResponse, responseLength);

            // Step 4: Execute the insert statement
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Step 5: Close the resources
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
