import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import javax.sql.DataSource;
import java.util.concurrent.CompletableFuture;

public class DB2DataInserter implements Runnable {

    private final List<byte[]> blobDataList;
    private final List<String> clobDataList;
    private final DataSource dataSource;
    private final Random random;

    private final int threadId;
    private final int iterations;

    public DB2DataInserter(int threadId, int iterations, List<byte[]> blobDataList, List<String> clobDataList, DataSource dataSource) {
        this.threadId = threadId;
        this.iterations = iterations;
        this.blobDataList = blobDataList;
        this.clobDataList = clobDataList;
        this.dataSource = dataSource;
        this.random = new Random();
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false); // Start transaction

            try (PreparedStatement pstmt1 = connection.prepareStatement(SQLStatements.INSERT_TABLE1);
                 PreparedStatement pstmt2 = connection.prepareStatement(SQLStatements.INSERT_TABLE2)) {

                for (int i = 0; i < iterations; i++) {
                    // Randomly select BLOB and CLOB data
                    byte[] blobData = blobDataList.get(random.nextInt(blobDataList.size()));
                    String clobData = clobDataList.get(random.nextInt(clobDataList.size()));

                    // Perform async inserts
                    CompletableFuture<Void> insert1 = insertIntoTableAsync(pstmt1, "Value1_" + threadId + "_" + i, "Value2_" + threadId + "_" + i, clobData, blobData);
                    CompletableFuture<Void> insert2 = insertIntoTableAsync(pstmt2, "Value3_" + threadId + "_" + i, "Value4_" + threadId + "_" + i, clobData, blobData);

                    // Wait for both inserts to complete
                    CompletableFuture.allOf(insert1, insert2).join();
                }

                connection.commit(); // Commit transaction
                System.out.println("Thread " + threadId + " completed successfully.");

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

    private CompletableFuture<Void> insertIntoTableAsync(PreparedStatement pstmt, String value1, String value2, String clobData, byte[] blobData) {
        return CompletableFuture.runAsync(() -> {
            try {
                synchronized (pstmt) {
                    pstmt.setString(1, value1);
                    pstmt.setString(2, value2);
                    pstmt.setString(3, clobData);
                    pstmt.setBytes(4, blobData);
                    pstmt.addBatch();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
