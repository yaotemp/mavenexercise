import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.List;

public class YamlToPojoExample {
    public static void main(String[] args) {
        // Configure the constructor to use the root class
        Constructor constructor = new Constructor(Config.class);

        // Set up the Yaml object with the custom constructor
        Yaml yaml = new Yaml(constructor);

        // Load the YAML file
        try (InputStream inputStream = YamlToPojoExample.class.getClassLoader().getResourceAsStream("config.yml")) {
            Config config = yaml.load(inputStream);
            System.out.println("Thread Pool Size: " + config.getThreadPoolSize());
            System.out.println("Execution Time Minutes: " + config.getExecutionTimeMinutes());
            for (MethodConfig method : config.getMethods()) {
                System.out.println("Method Name: " + method.getName());
                System.out.println("Parameters: " + method.getParameters());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
