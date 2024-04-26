import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LoggerManager {

    public static void configureLogger(String loggerName, String propertiesFileName) {
        Logger logger = Logger.getLogger(loggerName);
        try (FileInputStream fis = new FileInputStream(propertiesFileName)) {
            LogManager.getLogManager().readConfiguration(fis);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not load and apply properties file for " + loggerName, e);
        }
        logger.setUseParentHandlers(false);
    }

    public static Logger getLogger(String loggerName, String propertiesFileName) {
        configureLogger(loggerName, propertiesFileName);
        return Logger.getLogger(loggerName);
    }
}
