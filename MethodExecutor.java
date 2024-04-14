import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MethodExecutor {
    public static void main(String[] args) throws Exception {
        Yaml yaml = new Yaml(new Constructor(List.class));
        InputStream inputStream = MethodExecutor.class.getClassLoader().getResourceAsStream("methods.yml");
        if (inputStream == null) {
            throw new RuntimeException("Cannot find 'methods.yml' in the classpath");
        }
        List<Map<String, Object>> data = yaml.load(inputStream);

        ExecutorService executor = Executors.newFixedThreadPool(3); // Using 3 threads

        for (Map<String, Object> item : data) {
            executor.submit(() -> {
                try {
                    String methodName = (String) item.get("name");
                    List<Object> params = (List<Object>) item.get("parameters");
                    Class<?>[] paramTypes = new Class[params.size()];
                    for (int i = 0; i < params.size(); i++) {
                        paramTypes[i] = params.get(i).getClass();
                    }
                    Method method = MethodExecutor.class.getMethod(methodName, paramTypes);
                    method.invoke(null, params.toArray());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
    }

    public static void computeSum(Integer a, Integer b) {
        System.out.println("Sum of " + a + " and " + b + " is " + (a + b));
    }

    public static void printMessage(String message) {
        System.out.println(message);
    }

    public static void calculateDifference(Integer a, Integer b) {
        System.out.println("Difference between " + a + " and " + b + " is " + (a - b));
    }
}
