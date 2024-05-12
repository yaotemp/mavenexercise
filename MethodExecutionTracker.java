import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MethodExecutionTracker {
    static class AppConfig {
        private List<MethodConfig> methodList;
        private DbConfig dbConfig;

        public AppConfig(List<MethodConfig> methodList, DbConfig dbConfig) {
            this.methodList = methodList;
            this.dbConfig = dbConfig;
        }

        public List<MethodConfig> getMethodList() {
            return methodList;
        }

        public DbConfig getDbConfig() {
            return dbConfig;
        }
    }

    static class MethodConfig {
        private String methodName;
        private String parameters; // Parameters as a single string
        private int repeatTimes;

        public MethodConfig(String methodName, String parameters, int repeatTimes) {
            this.methodName = methodName;
            this.parameters = parameters;
            this.repeatTimes = repeatTimes;
        }

        public String getMethodName() {
            return methodName;
        }

        public String getParameters() {
            return parameters;
        }

        public int getRepeatTimes() {
            return repeatTimes;
        }

        // Method to simulate execution
        public void execute() {
            List<String> parameterList = Arrays.asList(parameters.split(","));
            System.out.println("Executing " + methodName + " with parameters " + parameterList);
        }
    }

    static class DbConfig {
        private String connection;
        private String username;
        private String password;

        public DbConfig(String connection, String username, String password) {
            this.connection = connection;
            this.username = username;
            this.password = password;
        }

        public String getConnection() {
            return connection;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    public static void main(String[] args) {
        AppConfig config = new AppConfig(
            new ArrayList<>(List.of(
                new MethodConfig("method1", "param1,param2", 2),
                new MethodConfig("method2", "param3,param4", 1)
            )),
            new DbConfig("jdbc:mysql://localhost:3306/mydb", "user", "password")
        );

        List<MethodConfig> executionList = new ArrayList<>(config.getMethodList());
        Collections.shuffle(executionList);

        // Execute methods
        executionList.forEach(MethodConfig::execute);

        // Reset repeatTimes and prepare new AppConfig for output
        executionList.forEach(method -> method.repeatTimes = 1);
        AppConfig newConfig = new AppConfig(executionList, config.getDbConfig());

        // Serialize the entire new AppConfig to YAML
        writeConfigToYaml(newConfig);
    }

    private static void writeConfigToYaml(AppConfig config) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(new Constructor(AppConfig.class), new Representer(), options);

        try (FileWriter writer = new FileWriter("new_config.yml")) {
            yaml.dump(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
