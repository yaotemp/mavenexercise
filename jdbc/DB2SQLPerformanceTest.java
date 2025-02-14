import org.yaml.snakeyaml.Yaml;
import java.io.*;
import java.sql.*;
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

        // Output file for storing results
        String outputFile = "output/query_results.txt";

        // Ensure the output directory exists
        File outputDir = new File("output");
        if (!outputDir.exists()) {
            if (outputDir.mkdirs()) {
                System.out.println("Output directory created: " + outputDir.getAbsolutePath());
            } else {
                System.err.println("Failed to create output directory: " + outputDir.getAbsolutePath());
                return;
            }
        }

        // Execute each query
        Connection conn = null;
        try {
            // Load the DB2 JDBC driver
            Class.forName("com.ibm.db2.jcc.DB2Driver");

            // Establish the connection
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            // Open the output file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                for (String query : queries) {
                    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                        // Set parameters if needed (example for the first parameter)
                        pstmt.setString(1, "some_value");

                        // Start the query execution timer
                        long queryStartTime = System.currentTimeMillis();

                        // Execute the query
                        ResultSet rs = pstmt.executeQuery();

                        // Stop the query execution timer
                        long queryEndTime = System.currentTimeMillis();

                        // Calculate the query execution time
                        long queryExecutionTime = queryEndTime - queryStartTime;

                        // Start the result writing timer
                        long writingStartTime = System.currentTimeMillis();

                        // Write the query and execution time to the file
                        writer.write("Query: " + query);
                        writer.newLine();
                        writer.write("Query Execution Time: " + queryExecutionTime + " ms");
                        writer.newLine();

                        // Get metadata to determine column types
                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();

                        // Write column headers
                        for (int i = 1; i <= columnCount; i++) {
                            writer.write(metaData.getColumnName(i) + "\t");
                        }
                        writer.newLine();

                        // Process the result set
                        while (rs.next()) {
                            for (int i = 1; i <= columnCount; i++) {
                                int columnType = metaData.getColumnType(i);

                                // Handle CLOB and BLOB fields
                                if (columnType == Types.CLOB) {
                                    Clob clob = rs.getClob(i);
                                    writer.write(clob != null ? clob.getSubString(1, (int) clob.length()) : "NULL");
                                } else if (columnType == Types.BLOB) {
                                    Blob blob = rs.getBlob(i);
                                    writer.write(blob != null ? "[BLOB Data]" : "NULL");
                                } else {
                                    // Handle other types (e.g., String, Integer, etc.)
                                    writer.write(rs.getString(i) != null ? rs.getString(i) : "NULL");
                                }
                                writer.write("\t");
                            }
                            writer.newLine();
                        }

                        // Stop the result writing timer
                        long writingEndTime = System.currentTimeMillis();

                        // Calculate the result writing time
                        long resultWritingTime = writingEndTime - writingStartTime;

                        // Write the result writing time to the file
                        writer.write("Result Writing Time: " + resultWritingTime + " ms");
                        writer.newLine();
                        writer.newLine(); // Separate results of different queries

                        // Close the ResultSet
                        rs.close();
                    }
                }
            }

        } catch (ClassNotFoundException e) {
            System.err.println("DB2 JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("SQL Exception occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error writing to the output file.");
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
