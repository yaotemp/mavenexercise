import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class multipleThreadInsert2 {

    private static final String DB_URL = "jdbc:db2://your_database_url:50000/your_database_name";
    private static final String USER = "your_username";
    private static final String PASS = "your_password";
    private static final int NUMBER_OF_THREADS = 10; // Adjust as needed
    private static final long DESIRED_RECORD_COUNT = 5000000; // Max records to process

    public static void main(String[] args) {
        long totalCount = getTotalCount();
        long recordCount = Math.min(totalCount, DESIRED_RECORD_COUNT); // Cap at desired record count
        long chunkSize = (recordCount + NUMBER_OF_THREADS - 1) / NUMBER_OF_THREADS; // Calculate chunk size using ceiling division

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            long startRow = i * chunkSize + 1; // Calculate the starting row for this chunk
            long endRow = Math.min((i + 1) * chunkSize, recordCount); // Calculate the ending row for this chunk
            Thread thread = new Thread(new Worker(startRow, endRow)); // Create a new worker thread
            threads.add(thread);
            thread.start(); // Start the thread
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join(); // Wait for the thread to finish
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static long getTotalCount() {
        long totalCount = 0;
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "SELECT COUNT(*) AS total_count FROM Tax_Return_Stats";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    totalCount = rs.getLong("total_count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalCount; // Return the total count of records
    }

    static class Worker implements Runnable {
        private final long startRow;
        private final long endRow;

        public Worker(long startRow, long endRow) {
            this.startRow = startRow;
            this.endRow = endRow;
        }

        @Override
        public void run() {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                String sql = "SELECT * FROM (" +
                             "SELECT ROW_NUMBER() OVER () AS row_num, * FROM Tax_Return_Stats" +
                             ") AS temp WHERE row_num BETWEEN ? AND ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setLong(1, startRow); // Set the start row parameter
                    pstmt.setLong(2, endRow); // Set the end row parameter
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            TaxReturnStats record = TaxReturnStats.fromResultSet(rs); // Convert ResultSet to TaxReturnStats object
                            processRecord(record); // Process the record
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void processRecord(TaxReturnStats record) {
            System.out.println("Thread: " + Thread.currentThread().getName() + " - Processing record with ID: " + record.getRetUnqKey());
        }
    }

}
