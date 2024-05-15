import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MultiThreadedJdbcTransaction {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/yourdatabase";
    private static final String JDBC_USER = "yourusername";
    private static final String JDBC_PASSWORD = "yourpassword";

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {
            executorService.execute(new JdbcTransactionTask("Task-" + i));
        }

        executorService.shutdown();

        // Wait for all tasks to complete before replaying transactions
        while (!executorService.isTerminated()) {
            // Busy-wait
        }

        // Replay transactions from the log files
        for (int i = 0; i < 5; i++) {
            replayTransactions("Task-" + i + ".log");
        }
    }

    static class JdbcTransactionTask implements Runnable {

        private final String taskName;
        private final Logger logger;
        private final Gson gson = new Gson();

        public JdbcTransactionTask(String taskName) {
            this.taskName = taskName;
            this.logger = Logger.getLogger(taskName);
            setupLogger();
        }

        private void setupLogger() {
            try {
                FileHandler fileHandler = new FileHandler(taskName + ".log", true);
                fileHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(fileHandler);
                logger.setLevel(Level.INFO);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            Connection connection = null;
            try {
                connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
                connection.setAutoCommit(false);

                // Example transaction
                performTransaction(connection, "INSERT INTO yourtable (column1, column2) VALUES (?, ?)", "value1", "value2");

                connection.commit();
            } catch (SQLException e) {
                if (connection != null) {
                    try {
                        connection.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void performTransaction(Connection connection, String query, Object... params) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
                logTransaction(query, params);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void logTransaction(String methodName, Object... params) {
            JsonObject logEntry = new JsonObject();
            logEntry.addProperty("method", methodName);
            logEntry.add("params", gson.toJsonTree(params));

            synchronized (logger) {
                logger.info(gson.toJson(logEntry));
            }
        }
    }

    public static void replayTransactions(String logFileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(logFileName))) {
            String line;
            Gson gson = new Gson();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                JsonObject logEntry = gson.fromJson(line, JsonObject.class);
                String methodName = logEntry.get("method").getAsString();
                Object[] params = gson.fromJson(logEntry.get("params"), Object[].class);

                replayTransaction(methodName, params);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void replayTransaction(String methodName, Object... params) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(methodName)) {
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
                preparedStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
