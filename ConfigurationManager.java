public class ConfigurationManager {
    private static volatile Config configInstance = null;

    // Private constructor to prevent instantiation
    private ConfigurationManager() {
    }

    // Initialize the configuration singleton with the specified config file
    public static void initialize(String configFilePath) {
        if (configInstance == null) {
            synchronized (ConfigurationManager.class) {
                if (configInstance == null) {
                    configInstance = loadConfig(configFilePath);
                }
            }
        }
    }

    // Public method to get the instance
    public static Config getConfig() {
        if (configInstance == null) {
            throw new IllegalStateException("Configuration has not been initialized.");
        }
        return configInstance;
    }

    // Method to load configuration, assumed to be static for simplicity
    private static Config loadConfig(String configFilePath) {
        Yaml yaml = new Yaml(new Constructor(Config.class));
        try (InputStream inputStream = ConfigurationManager.class.getClassLoader().getResourceAsStream(configFilePath)) {
            return yaml.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file", e);
        }
    }
}
