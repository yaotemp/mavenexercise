import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class db2Connection {

    public static void main(String[] args) {
        // URL to connect to your DB2 database. It typically looks like:
        // jdbc:db2://hostname:port/databaseName
        String dbURL = "jdbc:db2://yourhost:yourport/yourdb";

        // Database credentials
        String user = "yourusername";
        String password = "yourpassword";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Register JDBC driver
            Class.forName("com.ibm.db2.jcc.DB2Driver");

            // Open a connection
            System.out.println("Connecting to the database...");
            connection = DriverManager.getConnection(dbURL, user, password);

            // Execute a query
            System.out.println("Creating statement...");
            statement = connection.createStatement();
            String sql = "SELECT id, name FROM yourTable";
            resultSet = statement.executeQuery(sql);

            // Extract data from result set
            while (resultSet.next()) {
                // Retrieve by column name
                int id  = resultSet.getInt("id");
                String name = resultSet.getString("name");

                // Display values
                System.out.print("ID: " + id);
                System.out.println(", Name: " + name);
            }
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // finally block used to close resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }
}
