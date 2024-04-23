import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DataReader {

    private List<String[]> readResultsFromFile(String directory, String fileName) {
        List<String[]> records = new ArrayList<>();
        String fullPath = Paths.get(directory, fileName).toString(); // Create full path

        try (BufferedReader reader = new BufferedReader(new FileReader(fullPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split each line by comma and optional whitespace
                String[] values = line.split(",\\s*");
                records.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    public static void main(String[] args) {
        DataReader dataReader = new DataReader();
        String directory = "path/to/your/directory"; // Specify the directory
        String fileName = "output.txt"; // Specify the file name
        List<String[]> results = dataReader.readResultsFromFile(directory, fileName);

        // Example: Print the read results
        for (String[] row : results) {
            System.out.println(String.join(", ", row));
        }
    }
}
