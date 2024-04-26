import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LoggerManager {
    private static final ConcurrentHashMap<String, Logger> loggerCache = new ConcurrentHashMap<>();

    public static Logger getLogger(String loggerName, String propertiesFileName) {
        // Use computeIfAbsent to ensure the logger is configured only once per unique loggerName
        return loggerCache.computeIfAbsent(loggerName, name -> {
            configureLogger(name, propertiesFileName);
            return Logger.getLogger(name);
        });
    }

    private static void configureLogger(String loggerName, String propertiesFileName) {
        Logger logger = Logger.getLogger(loggerName);
        try (FileInputStream fis = new FileInputStream(propertiesFileName)) {
            LogManager.getLogManager().readConfiguration(fis);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not load and apply properties file for " + loggerName, e);
        }
        logger.setUseParentHandlers(false);
    }
}
