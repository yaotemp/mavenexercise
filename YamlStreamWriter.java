/*
 * 
 * <dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-yaml</artifactId>
    <version>2.13.0</version> <!-- Check for the latest version -->
</dependency>

 * 
 */


import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

public class YamlStreamWriter {
    public static void main(String[] args) {
        YAMLFactory yamlFactory = new YAMLFactory();
        ObjectMapper mapper = new ObjectMapper(yamlFactory);

        try {
            JsonGenerator generator = yamlFactory.createGenerator(new File("output.yaml"), JsonEncoding.UTF8);
            generator.setCodec(mapper);
            generator.writeStartObject(); // start the root object

            generator.writeFieldName("methods");
            generator.writeStartArray(); // start array of methods
            // Simulating streaming write of method configurations
            for (int i = 0; i < 100; i++) { // this could be iterating over a large list or database results
                generator.writeStartObject();
                generator.writeStringField("methodName", "method" + i);
                generator.writeStringField("parameters", "param1,param2");
                generator.writeNumberField("repeatTimes", 1);
                generator.writeEndObject();
            }
            generator.writeEndArray(); // end array

            generator.writeFieldName("database");
            generator.writeStartObject();
            generator.writeStringField("connection", "jdbc:mysql://localhost:3306/mydb");
            generator.writeStringField("username", "user");
            generator.writeStringField("password", "password");
            generator.writeEndObject();

            generator.writeEndObject(); // end the root object
            generator.close(); // close the generator and flush data to file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
