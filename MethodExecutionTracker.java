import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MethodExecutionTracker {
    static class MethodConfig {
        private String methodName;
        private String parameters;
        private int repeatTimes;

        public MethodConfig(String methodName, String parameters, int repeatTimes) {
            this.methodName = methodName;
            this.parameters = parameters;
            this.repeatTimes = repeatTimes;
        }
    }

    private static void writeConfigToYaml(List<MethodConfig> methodConfigs) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(new Constructor(MethodConfig.class), new Representer(), options);

        // Define the output directory
        String outputDirectory = "path/to/output/directory"; // Modify this path as needed
        // Create a timestamp
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = now.format(formatter);
        // Construct the full path with timestamp
        String filePath = outputDirectory + "/config_" + timestamp + ".yml";

        try (FileWriter writer = new FileWriter(filePath)) {
            yaml.dumpAll(methodConfigs.iterator(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Example usage
    public static void main(String[] args) {
        List<MethodConfig> methodConfigs = List.of(
            new MethodConfig("methodName1", "parameters1", 1),
            new MethodConfig("methodName2", "parameters2", 1)
        );

        writeConfigToYaml(methodConfigs);
    }
}
