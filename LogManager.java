import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LogManager {
    private static final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
    private static volatile boolean finished = false;
    private static BufferedWriter logWriter;

    public static void initializeLogger(String folderPath, String fileName) {
        try {
            File logDirectory = new File(folderPath);
            if (!logDirectory.exists()) {
                logDirectory.mkdirs(); // Create the directory if it does not exist
            }
            File logFile = new File(logDirectory, fileName);
            // Open the log file in append mode
            logWriter = new BufferedWriter(new FileWriter(logFile, true));
        } catch (IOException e) {
            throw new RuntimeException("Unable to open log file: " + folderPath + "/" + fileName, e);
        }
    }

    public static void log(String message) {
        try {
            logQueue.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void processLogs() {
        new Thread(() -> {
            while (!finished || !logQueue.isEmpty()) {
                try {
                    String logEntry = logQueue.take();
                    logWriter.write(logEntry + "\n");
                    logWriter.flush();  // Ensure data is written to file
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                logWriter.close();  // Close the writer when finished
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "Log-Processor-Thread").start();
    }

    public static void stopLogging() {
        finished = true;
    }
}
