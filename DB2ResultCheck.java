import java.sql.*;

public class DB2ResultCheck {
    public static void main(String[] args) {
        String url = "jdbc:db2://localhost:50000/MYDB"; // Replace with your DB2 database URL
        String user = "db2user"; // Replace with your username
        String password = "db2password"; // Replace with your password
        String query = "SELECT * FROM your_table"; // Replace with your actual SQL query

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                // Attempt to move to the second record to see if there are multiple records
                if (rs.next()) {
                    System.out.println("The result is a list of multiple records.");
                } else {
                    // Only one record returned, check column count
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    if (columnCount > 1) {
                        System.out.println("The result is a single record with multiple columns.");
                    } else {
                        System.out.println("The result is a single record with one column.");
                    }
                }
            } else {
                System.out.println("No records found.");
            }

        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        }
    }
}
