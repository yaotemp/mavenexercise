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

        List<String> result = new ArrayList<>();
        result.add("date,next_1_day,next_7_days,next_30_days"); // CSV header

        LocalDate today = LocalDate.now();
        LocalDate todayLastYear = today.minusYears(1);

        for (int offset = 0; offset < 30; offset++) {
            LocalDate currentDate = todayLastYear.plusDays(offset);

            // Handle wrap-around to January
            LocalDate mappedDate = mapDateToLastYear(currentDate);

            // Calculate sums
            int next1Day = trackMap.getOrDefault(mappedDate.plusDays(1), 0);
            int next7Days = sumTracksInRange(trackMap, mappedDate, 1, 7);
            int next30Days = sumTracksInRange(trackMap, mappedDate, 1, 30);

            result.add(String.format("%s,%d,%d,%d", mappedDate, next1Day, next7Days, next30Days));
        }

        return result;
    }

    public static int sumTracksInRange(Map<LocalDate, Integer> trackMap, LocalDate startDate, int startOffset, int endOffset) {
        int sum = 0;
        for (int i = startOffset; i <= endOffset; i++) {
            LocalDate targetDate = startDate.plusDays(i);

            // Handle wrap-around
            targetDate = mapDateToLastYear(targetDate);

            sum += trackMap.getOrDefault(targetDate, 0);
        }
        return sum;
    }

    public static LocalDate mapDateToLastYear(LocalDate date) {
        // If the date wraps to the next year, adjust it to the start of the last year
        if (date.getYear() > LocalDate.now().getYear() - 1) {
            return date.minusYears(1);
        }
        return date;
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

