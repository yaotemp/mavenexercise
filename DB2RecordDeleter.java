import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;

public class DB2RecordDeleter implements Runnable {

    private final DataSource dataSource;
    private final String deleteSQL;
    private final int batchSize;
    private final int threadId;

    public DB2RecordDeleter(int threadId, DataSource dataSource, String deleteSQL, int batchSize) {
        this.threadId = threadId;
        this.dataSource = dataSource;
        this.deleteSQL = deleteSQL;
        this.batchSize = batchSize;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false); // Start transaction

            try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
                boolean recordsDeleted;
                int totalDeleted = 0;

                do {
                    pstmt.setInt(1, batchSize);
                    int rowsDeleted = pstmt.executeUpdate();
                    totalDeleted += rowsDeleted;

                    recordsDeleted = rowsDeleted > 0;

                    connection.commit(); // Commit after each batch

                    System.out.println("Thread " + threadId + " deleted " + rowsDeleted + " records.");

                } while (recordsDeleted);

                System.out.println("Thread " + threadId + " completed successfully with total " + totalDeleted + " records deleted.");

            } catch (SQLException e) {
                connection.rollback(); // Rollback transaction on error
                System.out.println("Thread " + threadId + " rolled back due to error.");
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("Thread " + threadId + " took " + duration + " ms to complete.");
        }
    }
}
