import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

class DailyTrack {
    LocalDate date;
    int usedTracks;

    public DailyTrack(LocalDate date, int usedTracks) {
        this.date = date;
        this.usedTracks = usedTracks;
    }
}

public class TrackSumCalculator {

    public static void main(String[] args) throws IOException {
        String csvFile = "daily_tracks.csv"; // Path to your CSV file
        List<DailyTrack> trackData = readCsv(csvFile);

        List<String> result = calculateSums(trackData);

        // Print results to the console
        result.forEach(System.out::println);

        // Optionally write the result to a file
        writeResultsToFile("track_summary.csv", result);
    }

    public static List<DailyTrack> readCsv(String filePath) throws IOException {
        List<DailyTrack> trackData = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                LocalDate date = LocalDate.parse(parts[0], formatter);
                int usedTracks = Integer.parseInt(parts[1]);
                trackData.add(new DailyTrack(date, usedTracks));
            }
        }
        return trackData;
    }

    public static List<String> calculateSums(List<DailyTrack> trackData) {
        // Sort the data by date
        trackData.sort(Comparator.comparing(track -> track.date));

        // Create a map for quick lookups
        Map<LocalDate, Integer> trackMap = trackData.stream()
                .collect(Collectors.toMap(track -> track.date, track -> track.usedTracks));

        // Get the year of the dataset
        int datasetYear = trackData.get(0).date.getYear();

        List<String> result = new ArrayList<>();
        result.add("date,next_1_day,next_7_days,next_30_days"); // CSV header

        for (DailyTrack track : trackData) {
            LocalDate date = track.date;
            int next1Day = trackMap.getOrDefault(wrapToYear(date.plusDays(1), datasetYear), 0);
            int next7Days = sumTracksInRange(trackMap, date, 1, 7, datasetYear);
            int next30Days = sumTracksInRange(trackMap, date, 1, 30, datasetYear);

            result.add(String.format("%s,%d,%d,%d", date, next1Day, next7Days, next30Days));
        }

        return result;
    }

    public static int sumTracksInRange(Map<LocalDate, Integer> trackMap, LocalDate startDate, int startOffset, int endOffset, int datasetYear) {
        int sum = 0;
        for (int i = startOffset; i <= endOffset; i++) {
            LocalDate targetDate = wrapToYear(startDate.plusDays(i), datasetYear);
            sum += trackMap.getOrDefault(targetDate, 0);
        }
        return sum;
    }

    public static LocalDate wrapToYear(LocalDate date, int year) {
        // Adjust the date to the specified year, wrapping month and day as needed
        return LocalDate.of(year, date.getMonth(), date.getDayOfMonth());
    }

    public static void writeResultsToFile(String filePath, List<String> result) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : result) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}
