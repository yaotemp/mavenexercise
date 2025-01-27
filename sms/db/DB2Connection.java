import java.sql.*;

public class DB2Connection {

    public static void main(String[] args) {

        try {
            // Load the DB2 JDBC driver
            Class.forName("com.ibm.db2.jcc.DB2Driver");

            // Create the connection URL
            String url = "jdbc:db2://localhost:50000/MYDB"; // Replace with your DB2 details

            // Connect to the database
            Connection conn = DriverManager.getConnection(url);

            // Execute a simple query 
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1 FROM SYSIBM.SYSDUMMY1");

            while (rs.next()) {
                System.out.println("Connected successfully!");
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}