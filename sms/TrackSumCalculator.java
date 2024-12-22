import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TrackSumCalculator {

    public static void main(String[] args) {
        String inputCsvFile = "daily_tracks.csv"; // Path to your input CSV file
        String outputCsvFile = "track_summary.csv"; // Path to your output CSV file

        try {
            LocalDate today = LocalDate.now(); // Today's date
            processCsv(inputCsvFile, outputCsvFile, today);
            System.out.println("Summary written to " + outputCsvFile);
        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
        }
    }

    public static void processCsv(String inputCsvFile, String outputCsvFile, LocalDate today) throws IOException {
        // Map to hold data for the previous year's relevant range of dates
        TreeMap<LocalDate, Integer> lastYearData = new TreeMap<>();

        // Prepare the output file
        try (BufferedReader br = new BufferedReader(new FileReader(inputCsvFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputCsvFile))) {

            // Read the header
            String header = br.readLine();
            if (header == null) {
                throw new IOException("CSV file is empty.");
            }

            // Write the new header to the output file
            bw.write("date,next_1_day,next_7_days,next_30_days");
            bw.newLine();

            // Date formatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Read all data into the map
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                LocalDate date = LocalDate.parse(parts[0], formatter);
                int usedTracks = Integer.parseInt(parts[1]);
                lastYearData.put(date, usedTracks);
            }

            // Calculate the results based on last year's corresponding date range
            LocalDate todayLastYear = today.minusYears(1);
            for (LocalDate currentDate = todayLastYear;
                 currentDate.isBefore(todayLastYear.plusDays(31));
                 currentDate = currentDate.plusDays(1)) {

                // Calculate sums for the current date
                int next1Day = lastYearData.getOrDefault(currentDate.plusDays(1), 0);
                int next7Days = sumTracksInRange(lastYearData, currentDate, 1, 7);
                int next30Days = sumTracksInRange(lastYearData, currentDate, 1, 30);

                // Write the result to the output file
                bw.write(String.format("%s,%d,%d,%d", currentDate, next1Day, next7Days, next30Days));
                bw.newLine();
            }
        }
    }

    public static int sumTracksInRange(TreeMap<LocalDate, Integer> data, LocalDate startDate, int startOffset, int endOffset) {
        int sum = 0;
        for (int i = startOffset; i <= endOffset; i++) {
            sum += data.getOrDefault(startDate.plusDays(i), 0);
        }
        return sum;
    }
}

