import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import javax.sql.DataSource;

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

            try {
                for (int i = 0; i < iterations; i++) {
                    // Randomly select BLOB and CLOB data
                    byte[] blobData = blobDataList.get(random.nextInt(blobDataList.size()));
                    String clobData = clobDataList.get(random.nextInt(clobDataList.size()));

                    // Call methods to insert data into each table
                    insertIntoTable1(connection, "Value1_" + threadId + "_" + i, "Value2_" + threadId + "_" + i, clobData, blobData);
                    insertIntoTable2(connection, "Value3_" + threadId + "_" + i, "Value4_" + threadId + "_" + i, clobData, blobData);
                    // Call insert methods for other tables
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

    private void insertIntoTable1(Connection connection, String value1, String value2, String clobData, byte[] blobData) throws SQLException {
        String sql = SQLStatements.INSERT_TABLE1;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, value1);
            pstmt.setString(2, value2);
            pstmt.setString(3, clobData);
            pstmt.setBytes(4, blobData);
            pstmt.executeUpdate();
        }
    }

    private void insertIntoTable2(Connection connection, String value1, String value2, String clobData, byte[] blobData) throws SQLException {
        String sql = SQLStatements.INSERT_TABLE2;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, value1);
            pstmt.setString(2, value2);
            pstmt.setString(3, clobData);
            pstmt.setBytes(4, blobData);
            pstmt.executeUpdate();
        }
    }

    // Define more methods for other tables as needed
}


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class Main {

    public static void main(String[] args) {
        DataSource dataSource = DataSourceFactory.getDataSource();

        try {
            // Read BLOB and CLOB data from directories
            List<byte[]> blobDataList = readBlobFiles("/test/blob");
            List<String> clobDataList = readClobFiles("/test/clob");

            int numberOfThreads = 200;
            int iterationsPerThread = 10000000;

            Thread[] threads = new Thread[numberOfThreads];

            for (int i = 0; i < numberOfThreads; i++) {
                threads[i] = new Thread(new DB2DataInserter(i, iterationsPerThread, blobDataList, clobDataList, dataSource));
                threads[i].start();
            }

            for (int i = 0; i < numberOfThreads; i++) {
                threads[i].join();
            }

            System.out.println("Data insertion completed.");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static List<byte[]> readBlobFiles(String directoryPath) throws IOException {
        List<byte[]> blobDataList = new ArrayList<>();
        Files.list(Paths.get(directoryPath)).forEach(path -> {
            try {
                blobDataList.add(Files.readAllBytes(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return blobDataList;
    }

    private static List<String> readClobFiles(String directoryPath) throws IOException {
        List<String> clobDataList = new ArrayList<>();
        Files.list(Paths.get(directoryPath)).forEach(path -> {
            try {
                clobDataList.add(new String(Files.readAllBytes(path)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return clobDataList;
    }
}
