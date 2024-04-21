import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class LogManager {
    private static final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
    private static final ExecutorService logExecutor = Executors.newSingleThreadExecutor();
    private static volatile boolean shutdown = false;
    private static Path logFilePath;

    public static void initialize(Path logFilePath) {
        LogManager.logFilePath = logFilePath;
        logExecutor.execute(() -> {
            try {
                while (!shutdown) {
                    String message = logQueue.take();
                    writeToLogFile(message);
                }
            } catch (InterruptedException e) {
                System.err.println("Logging thread interrupted");
                e.printStackTrace();
            }
        });
    }

    private static void writeToLogFile(String message) {
        try {
            Files.write(logFilePath, (message + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to write log message to file");
            e.printStackTrace();
        }
    }

    public static void log(String message) {
        try {
            logQueue.put(message);
        } catch (InterruptedException e) {
            System.err.println("Interrupted while adding message to log queue");
            e.printStackTrace();
        }
    }

    public static void shutdown() {
        shutdown = true;
        logExecutor.shutdown();
    }
}