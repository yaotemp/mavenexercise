import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DailyRecordsGenerator {

    public static void main(String[] args) {
        String inputFile = "input.csv";  // Input CSV file
        String outputFile = "daily_records.csv";  // Output CSV file
        
        // Read the input file and generate daily records
        try {
            List<String[]> dailyRecords = generateDailyRecords(inputFile);
            writeDailyRecords(outputFile, dailyRecords);
            System.out.println("Daily records have been saved to " + outputFile);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static List<String[]> generateDailyRecords(String inputFile) throws IOException {
        List<String[]> dailyRecords = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Read the input CSV
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) { // Skip the header line
                    isFirstLine = false;
                    continue;
                }

                // Parse the line
                String[] parts = line.split(",");
                String filename = parts[0];
                LocalDate beginDate = LocalDate.parse(parts[1], formatter);
                int days = Integer.parseInt(parts[2]);
                int growthRate = Integer.parseInt(parts[3]);

                // Generate daily records
                for (int i = 0; i < days; i++) {
                    LocalDate currentDate = beginDate.plusDays(i);
                    dailyRecords.add(new String[]{filename, currentDate.toString(), String.valueOf(growthRate)});
                }
            }
        }

        return dailyRecords;
    }

    public static void writeDailyRecords(String outputFile, List<String[]> dailyRecords) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            // Write the header
            writer.write("Filename,Date,Growth Rate");
            writer.newLine();

            // Write each record
            for (String[] record : dailyRecords) {
                writer.write(String.join(",", record));
                writer.newLine();
            }
        }
    }
}
