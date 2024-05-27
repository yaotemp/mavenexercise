import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
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

    // Centralized logger for general logs
    private static final Logger centralizedLogger = Logger.getLogger("CentralizedLogger");

    static {
        setupCentralizedLogger();
    }

    private static void setupCentralizedLogger() {
        try {
            FileHandler fileHandler = new FileHandler("centralized.log", true); // Append mode enabled
            fileHandler.setFormatter(new SimpleFormatter());
            centralizedLogger.addHandler(fileHandler);
            centralizedLogger.setLevel(Level.INFO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter the folder path: ");
        String folderPath = scanner.nextLine();

        System.out.print("Please enter the number of threads: ");
        int threadSize = scanner.nextInt();

        Path path = Paths.get(folderPath);

        // Check if the folder exists, if not, create it
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                centralizedLogger.info("The folder did not exist, so it was created.");
            } catch (IOException e) {
                centralizedLogger.severe("Failed to create the directory. Exiting.");
                e.printStackTrace();
                return;
            }
        }

        try {
            List<Path> dataFiles = listDataFiles(path);
            Collections.sort(dataFiles);

            if (dataFiles.size() < threadSize) {
                centralizedLogger.severe(String.format("Error: There are not enough .data files for the specified thread size (%d).", threadSize));
                centralizedLogger.severe(String.format("Number of .data files found: %d", dataFiles.size()));
                return;
            }

            centralizedLogger.info("Number of .data files: " + dataFiles.size());
            centralizedLogger.info("Sorted .data files:");
            for (Path file : dataFiles) {
                centralizedLogger.info(file.getFileName().toString());
            }

            ExecutorService executorService = Executors.newFixedThreadPool(threadSize);

            for (int i = 0; i < threadSize; i++) {
                executorService.execute(new JdbcTransactionTask("Task-" + i, folderPath));
            }

            executorService.shutdown();

            // Wait for all tasks to complete before replaying transactions
            while (!executorService.isTerminated()) {
                // Busy-wait
            }

            // Process each log file
            for (Path file : dataFiles) {
                replayTransactions(file);
            }

        } catch (IOException e) {
            centralizedLogger.severe("IOException occurred.");
            e.printStackTrace();
        }

        scanner.close();
    }

    public static List<Path> listDataFiles(Path path) throws IOException {
        List<Path> dataFiles = new ArrayList<>();

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".data")) {
                    dataFiles.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        return dataFiles;
    }

    static class JdbcTransactionTask implements Runnable {

        private final String taskName;
        private final String folderPath;
        private final Gson gson = new Gson();
        private final BufferedWriter writer;

        public JdbcTransactionTask(String taskName, String folderPath) {
            this.taskName = taskName;
            this.folderPath = folderPath;
            String logFileName = folderPath + "/" + taskName + "-" + System.currentTimeMillis() + ".log";
            this.writer = createWriter(logFileName);
        }

        private BufferedWriter createWriter(String logFileName) {
            try {
                return new BufferedWriter(new FileWriter(logFileName, false)); // Create a new file for each run
            } catch (IOException e) {
                throw new RuntimeException("Failed to create log writer", e);
            }
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
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
                long endTime = System.currentTimeMillis();
                logThreadSummary(startTime, endTime);
                closeWriter();
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

            try {
                writer.write(gson.toJson(logEntry));
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void logThreadSummary(long startTime, long endTime) {
            long duration = endTime - startTime;
            String summary = String.format("Thread %s completed in %d ms", taskName, duration);

            synchronized (centralizedLogger) {
                centralizedLogger.info(summary);
            }
        }

        private void closeWriter() {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void replayTransactions(Path logFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath.toFile()))) {
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
            centralizedLogger.severe("IOException occurred during replay.");
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
                centralizedLogger.severe("SQLException occurred during transaction.");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            centralizedLogger.severe("SQLException occurred while establishing connection.");
            e.printStackTrace();
        }
    }
}
