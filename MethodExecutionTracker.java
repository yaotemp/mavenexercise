import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MethodExecutionTracker {
    static class MethodConfig {
        private String methodName;
        private String parameters;
        private Integer repeatTimes;

        public MethodConfig(String methodName, String parameters, Integer repeatTimes) {
            this.methodName = methodName;
            this.parameters = parameters;
            this.repeatTimes = repeatTimes;
        }
    }

    private static void writeConfigToYaml(List<MethodConfig> methodConfigs) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        Representer representer = new Representer() {
            @Override
            protected Node representData(Object data) {
                // Check if the value is null or, in case of strings, empty
                if (data == null || (data instanceof String && ((String) data).isEmpty())) {
                    return super.representData(null);
                }
                // Check for empty collections
                if (data instanceof List && ((List<?>) data).isEmpty()) {
                    return super.representData(null);
                }
                return super.representData(data);
            }
        };

        Yaml yaml = new Yaml(representer, options);
        try (FileWriter writer = new FileWriter("new_config.yml")) {
            yaml.dumpAll(methodConfigs.iterator(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        List<MethodConfig> methodConfigs = new ArrayList<>();
        methodConfigs.add(new MethodConfig("methodName1", "", null));
        methodConfigs.add(new MethodConfig("methodName2", "param1,param2", 1));

        writeConfigToYaml(methodConfigs);
    }
}
