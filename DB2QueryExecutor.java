import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class DB2QueryExecutor {
    private Map<String, String> sqlQueries;
    private List<String> sqlToRun;
    private Connection connection;

    public DB2QueryExecutor() {
        connectToDatabase();
        loadYamlConfig();
    }

    private void connectToDatabase() {
        try {
            // Replace with your DB2 connection details
            String url = "jdbc:db2://yourhost:yourport/yourdb";
            connection = DriverManager.getConnection(url, "yourusername", "yourpassword");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadYamlConfig() {
        Yaml yaml = new Yaml(new Constructor(Map.class));
        try (InputStream inputStream = new FileInputStream(new File("config.yml"))) {
            Map<String, Object> yamlData = yaml.load(inputStream);
            sqlQueries = (Map<String, String>) yamlData.get("sqlQueries");
            Map<String, List<String>> config = (Map<String, List<String>>) yamlData.get("config");
            sqlToRun = config.get("sqlToRun");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void executeAndWriteSqlResults() {
        for (String key : sqlToRun) {
            String sql = sqlQueries.get(key);
            if (sql != null) {
                executeSqlAndWriteToFile(sql, key);
            } else {
                System.out.println("No SQL found for key: " + key);
            }
        }
    }

    private void executeSqlAndWriteToFile(String sql, String methodName) {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Example bind parameter
            stmt.setInt(1, 1); // Example user ID
            ResultSet rs = stmt.executeQuery();
            writeResultsToFile(rs, methodName + ".txt");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void writeResultsToFile(ResultSet rs, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    writer.write(rs.getString(i) + (i == columnCount ? "" : ", "));
                }
                writer.newLine();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DB2QueryExecutor executor = new DB2QueryExecutor();
        executor.executeAndWriteSqlResults();
    }
}
