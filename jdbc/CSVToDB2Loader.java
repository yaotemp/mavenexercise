import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CSVToDB2Loader {

    // DB2 connection details
    private static final String DB_URL = "jdbc:db2://<hostname>:<port>/<database>";
    private static final String DB_USER = "<username>";
    private static final String DB_PASSWORD = "<password>";

    // Directory containing CSV files
    private static final String CSV_DIRECTORY = "path/to/csv/files";

    // Target DB2 table
    private static final String DB_TABLE = "DASD_FREESPACE_RECORD";

    public static void main(String[] args) {
        // Get all CSV files in the directory
        File directory = new File(CSV_DIRECTORY);
        File[] csvFiles = directory.listFiles((dir, name) -> name.endsWith(".csv"));

        if (csvFiles == null || csvFiles.length == 0) {
            System.err.println("No CSV files found in the directory: " + CSV_DIRECTORY);
            return;
        }

        // Process each CSV file
        for (File csvFile : csvFiles) {
            System.out.println("Processing file: " + csvFile.getName());
            try {
                processCSVFile(csvFile);
            } catch (IOException | SQLException | ParseException e) {
                System.err.println("Error processing file: " + csvFile.getName());
                e.printStackTrace();
            }
        }
    }

    private static void processCSVFile(File csvFile) throws IOException, SQLException, ParseException {
        // Open the CSV file
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            // Read the first 2 lines to get the date value from B2
            String line;
            String queryDateStr = null;
            for (int i = 1; i <= 2; i++) {
                line = reader.readLine();
                if (i == 2) {
                    String[] cells = line.split(",");
                    if (cells.length > 1) {
                        queryDateStr = cells[1].trim(); // B2 cell (2nd row, 2nd column)
                    }
                }
            }

            if (queryDateStr == null || queryDateStr.isEmpty()) {
                throw new IOException("Query date not found in B2 cell.");
            }

            // Parse the query date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date queryDate = dateFormat.parse(queryDateStr);

            // Skip lines until line 13
            for (int i = 3; i < 13; i++) {
                reader.readLine();
            }

            // Connect to the DB2 database
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                // Prepare the SQL insert statement
                String sql = "INSERT INTO " + DB_TABLE + " (" +
                        "QUERY_DATE, VOLUME_SERIAL, MOUNT_ATTR, DEVICE_TYPE, FREE_EXTS, FREE_TRACKS, " +
                        "FREE_CYLS, LRG_FREE_TRACKS, LG_FREE_CYLS, DASD_NMBR, VTOC, SMS_INDX, SMS_IND, " +
                        "DENSITY, STORAGE_GROUP, VOLUME_FREE_PERCENT, DSCB_FREE_PERCENT, VIRS_FREE_PERCENT, " +
                        "CYLS_ON_VOL, SMS_VOL_STATUS, ALLOC_CNTR, FC_DS_FLAG" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    // Read and process data records
                    while ((line = reader.readLine()) != null) {
                        String[] cells = line.split(",");

                        // Set the query date
                        pstmt.setDate(1, new java.sql.Date(queryDate.getTime()));

                        // Set other columns based on the CSV structure
                        pstmt.setString(2, cells[0].trim()); // VOLUME_SERIAL
                        pstmt.setString(3, cells[1].trim()); // MOUNT_ATTR
                        pstmt.setString(4, cells[2].trim()); // DEVICE_TYPE
                        pstmt.setInt(5, Integer.parseInt(cells[3].trim())); // FREE_EXTS
                        pstmt.setInt(6, Integer.parseInt(cells[4].trim())); // FREE_TRACKS
                        pstmt.setInt(7, Integer.parseInt(cells[5].trim())); // FREE_CYLS
                        pstmt.setInt(8, Integer.parseInt(cells[6].trim())); // LRG_FREE_TRACKS
                        pstmt.setInt(9, Integer.parseInt(cells[7].trim())); // LG_FREE_CYLS
                        pstmt.setString(10, cells[8].trim()); // DASD_NMBR
                        pstmt.setString(11, cells[9].trim()); // VTOC
                        pstmt.setString(12, cells[10].trim()); // SMS_INDX
                        pstmt.setString(13, cells[11].trim()); // SMS_IND
                        pstmt.setString(14, cells[12].trim()); // DENSITY
                        pstmt.setString(15, cells[13].trim()); // STORAGE_GROUP
                        pstmt.setBigDecimal(16, new java.math.BigDecimal(cells[14].trim())); // VOLUME_FREE_PERCENT
                        pstmt.setBigDecimal(17, new java.math.BigDecimal(cells[15].trim())); // DSCB_FREE_PERCENT
                        pstmt.setBigDecimal(18, new java.math.BigDecimal(cells[16].trim())); // VIRS_FREE_PERCENT
                        pstmt.setInt(19, Integer.parseInt(cells[17].trim())); // CYLS_ON_VOL
                        pstmt.setString(20, cells[18].trim()); // SMS_VOL_STATUS
                        pstmt.setString(21, cells[19].trim()); // ALLOC_CNTR
                        pstmt.setString(22, cells[20].trim()); // FC_DS_FLAG

                        // Execute the insert statement
                        pstmt.executeUpdate();
                    }
                }
            }
        }
    }
}
