public class LoggerManager {
    static {
        configureLogger("SystemLogger", "systemLogger.properties");
        configureLogger("BusinessLogger", "businessLogger.properties");
    }

    public static void configureLogger(String loggerName, String propertiesFileName) {
        Logger logger = Logger.getLogger(loggerName);
        try (FileInputStream fis = new FileInputStream(propertiesFileName)) {
            LogManager.getLogManager().readConfiguration(fis);
        } catch (IOException e) {
            System.err.println("Could not load and apply properties file for " + loggerName + ": " + e.getMessage());
        }
        logger.setUseParentHandlers(false);
    }

    public static Logger getLogger(String loggerName) {
        return Logger.getLogger(loggerName);
    }
}
