import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.List;

public class JdbcTransactionTask implements Runnable {
    private final Config config;
    private final Map<String, List<String[]>> dataListMap;
    private final String taskName;
    private final BufferedWriter transactionWriter;
    private final BufferedWriter transactionSummaryWriter;
    private final BufferedWriter writer;

    public JdbcTransactionTask(Config config, Map<String, List<String[]>> dataListMap, String taskName) {
        this.config = config;
        this.dataListMap = dataListMap;
        this.taskName = taskName;

        // Ensure directories exist
        Utils.checkCreateFileFolder(config.getOutputDirectory());
        Utils.checkCreateFileFolder(config.getOutputDirectory() + "/replaydata");

        // Initialize log file names
        String transactionLogFileName = config.getOutputDirectory() + "/replaydata/" + taskName + "_method_data.data";
        String transactionSummaryLogFileName = config.getOutputDirectory() + "/" + taskName + "_method_data_result.log";
        String logFileName = config.getOutputDirectory() + "/" + taskName + ".log";

        // Initialize writers
        this.transactionWriter = createWriter(transactionLogFileName);
        this.transactionSummaryWriter = createWriter(transactionSummaryLogFileName);
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
        // Your JDBC transaction code here...
        long endTime = System.currentTimeMillis();
        logThreadSummary(startTime, endTime);
        closeWriters();
    }

    private void logTransaction(String methodName, Object... params) {
        JsonObject logEntry = new JsonObject();
        logEntry.addProperty("method", methodName);
        logEntry.add("params", gson.toJsonTree(params));

        try {
            transactionWriter.write(gson.toJson(logEntry));
            transactionWriter.newLine();
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

    private void closeWriters() {
        try {
            if (transactionWriter != null) {
                transactionWriter.close();
            }
            if (transactionSummaryWriter != null) {
                transactionSummaryWriter.close();
            }
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Utils {
    public static void checkCreateFileFolder(String folderPath) {
        Path path = Paths.get(folderPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory: " + folderPath, e);
            }
        }
    }
}

class Config {
    public String getOutputDirectory() {
        // Implement this method to return the output directory path
        return "/path/to/output/directory";
    }
}
