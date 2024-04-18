public class LogManager {
    private static final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
    private static volatile boolean finished = false;
    private static BufferedWriter logWriter;
    private static Thread logProcessor;

    public static void initializeLogger(String folderPath, String fileName) {
        try {
            File logDirectory = new File(folderPath);
            if (!logDirectory.exists()) {
                logDirectory.mkdirs(); // Create the directory if it does not exist
            }
            File logFile = new File(logDirectory, fileName);
            // Open the log file in append mode
            logWriter = new BufferedWriter(new FileWriter(logFile, true));
            startLogProcessor();
        } catch (IOException e) {
            throw new RuntimeException("Unable to open log file: " + folderPath + "/" + fileName, e);
        }
    }

    private static void startLogProcessor() {
        logProcessor = new Thread(() -> {
            try {
                while (!finished || !logQueue.isEmpty()) {
                    String logEntry = logQueue.take();
                    logWriter.write(logEntry + "\n");
                    logWriter.flush();  // Ensure data is written to file
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    logWriter.close();  // Close the writer when finished
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "Log-Processor-Thread");
        logProcessor.start();
    }

    public static void log(String message) {
        if (!finished) {  // Only log if the system hasn't been marked as finished
            try {
                logQueue.put(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void stopLogging() throws InterruptedException {
        finished = true;
        logProcessor.join(); // Wait for the logging thread to finish processing
    }
}
