package jdbc;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DB2SQLPerformanceTest {

    public static void main(String[] args) {
        // Load the YAML configuration
        Yaml yaml = new Yaml();
        InputStream inputStream = DB2SQLPerformanceTest.class
                .getClassLoader()
                .getResourceAsStream("config.yml");

        if (inputStream == null) {
            System.err.println("config.yml file not found!");
            return;
        }

        // Parse the YAML file
        Map<String, Object> config = yaml.load(inputStream);

        // Extract database connection details
        Map<String, String> dbConfig = (Map<String, String>) config.get("db");
        String dbUrl = dbConfig.get("url");
        String dbUser = dbConfig.get("user");
        String dbPassword = dbConfig.get("password");

        // Extract SQL queries
        List<String> queries = (List<String>) config.get("queries");

        // Execute each query
        Connection conn = null;
        try {
            // Load the DB2 JDBC driver
            Class.forName("com.ibm.db2.jcc.DB2Driver");

            // Establish the connection
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            for (String query : queries) {
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    // Set parameters if needed (example for the first parameter)
                    pstmt.setString(1, "some_value");

                    // Start the timer
                    long startTime = System.currentTimeMillis();

                    // Execute the query
                    try (ResultSet rs = pstmt.executeQuery()) {
                        // Stop the timer
                        long endTime = System.currentTimeMillis();

                        // Calculate the execution time
                        long executionTime = endTime - startTime;

                        // Print the execution time
                        System.out.println("Query: " + query);
                        System.out.println("Execution Time: " + executionTime + " ms");

                        // Process the result set (optional)
                        while (rs.next()) {
                            // Process each row (e.g., print or store results)
                        }
                    }
                }
            }

        } catch (ClassNotFoundException e) {
            System.err.println("DB2 JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("SQL Exception occurred.");
            e.printStackTrace();
        } finally {
            // Close the connection
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
