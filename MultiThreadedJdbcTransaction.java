import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
        private final Logger transactionLogger;
        private final Logger processingTimeLogger;
        private final Logger threadSummaryLogger;
        private final Gson gson = new Gson();

        public JdbcTransactionTask(String taskName) {
            this.taskName = taskName;
            this.transactionLogger = Logger.getLogger(taskName + ".transaction");
            this.processingTimeLogger = Logger.getLogger(taskName + ".processingTime");
            this.threadSummaryLogger = Logger.getLogger(taskName + ".summary");
            setupLoggers();
        }

        private void setupLoggers() {
            setupLogger(transactionLogger, taskName + ".log");
            setupLogger(processingTimeLogger, taskName + ".processingTime.log");
            setupLogger(threadSummaryLogger, taskName + ".summary.log");
        }

        private void setupLogger(Logger logger, String fileName) {
            try {
                FileHandler fileHandler = new FileHandler(fileName, true);
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

            synchronized (transactionLogger) {
                transactionLogger.info(gson.toJson(logEntry));
            }
        }
    }

    public static void replayTransactions(String logFileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(logFileName))) {
            List<String> batch = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                batch.add(line);

                if (batch.size() == 500) {
                    processBatch(batch);
                    batch.clear();
                }
            }
            // Process any remaining lines in the batch
            if (!batch.isEmpty()) {
                processBatch(batch);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processBatch(List<String> batch) {
        Gson gson = new Gson();
        for (String entry : batch) {
            JsonObject logEntry = gson.fromJson(entry, JsonObject.class);
            String methodName = logEntry.get("method").getAsString();
            Object[] params = gson.fromJson(logEntry.get("params"), Object[].class);

            replayTransaction(methodName, params);
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
