import java.sql.ResultSet;
import java.sql.SQLException;

public class TaxRecord {
    private int taxYear;
    private String taxReturnType;
    private String lobType;
    private String sizeInd;
    private int lowBound;
    private int upperBound;
    private String key;

    // Constructor
    public TaxRecord(int taxYear, String taxReturnType, String lobType, String sizeInd, int lowBound, int upperBound) {
        this.taxYear = taxYear;
        this.taxReturnType = taxReturnType;
        this.lobType = lobType;
        this.sizeInd = sizeInd;
        this.lowBound = lowBound;
        this.upperBound = upperBound;
        this.key = generateKey(taxYear, taxReturnType, lobType); // Generate the key during object creation
    }

    // Static factory method to create a TaxRecord from a ResultSet
    public static TaxRecord fromResultSet(ResultSet rs) throws SQLException {
        int taxYear = rs.getInt("Tax_Year");
        String taxReturnType = rs.getString("Tax_return_type");
        String lobType = rs.getString("lob_type");
        String sizeInd = rs.getString("Size_Ind");
        int lowBound = rs.getInt("Low_bound");
        int upperBound = rs.getInt("Upper_bound");

        return new TaxRecord(taxYear, taxReturnType, lobType, sizeInd, lowBound, upperBound);
    }

    // Getters
    public int getTaxYear() {
        return taxYear;
    }

    public String getTaxReturnType() {
        return taxReturnType;
    }

    public String getLobType() {
        return lobType;
    }

    public String getSizeInd() {
        return sizeInd;
    }

    public int getLowBound() {
        return lowBound;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public String getKey() {
        return key;
    }

    // Method to generate the key
    private String generateKey(int taxYear, String taxReturnType, String lobType) {
        return taxYear + "-" + taxReturnType + "-" + lobType;
    }

    @Override
    public String toString() {
        return "TaxRecord{" +
               "taxYear=" + taxYear +
               ", taxReturnType='" + taxReturnType + '\'' +
               ", lobType='" + lobType + '\'' +
               ", sizeInd='" + sizeInd + '\'' +
               ", lowBound=" + lowBound +
               ", upperBound=" + upperBound +
               ", key='" + key + '\'' +
               '}';
    }
}


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    // Map to store lists of TaxRecords with their computed key
    private static Map<String, List<TaxRecord>> recordMap = new HashMap<>();

    public static void main(String[] args) {
        try {
            // Example using JDBC to connect to the database and retrieve the data
            Connection conn = DriverManager.getConnection("jdbc:your_database_url", "username", "password");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM your_table_name");

            // Loop through the result set and create TaxRecord objects
            while (rs.next()) {
                TaxRecord record = TaxRecord.fromResultSet(rs);
                String key = record.getKey();

                // Add the TaxRecord to the list in the map, initializing the list if necessary
                recordMap.computeIfAbsent(key, k -> new ArrayList<>()).add(record);
            }

            rs.close();
            stmt.close();
            conn.close();

            // Example usage of finding a TaxRecord
            String key = "2023-1040-LOB1"; // Example key
            int docLength = 4500; // Example document length
            TaxRecord foundRecord = findTaxRecord(key, docLength);

            if (foundRecord != null) {
                System.out.println("Found TaxRecord: " + foundRecord);
            } else {
                System.out.println("No matching TaxRecord found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds a TaxRecord by key and docLength.
     * The docLength must be within the lowBound and upperBound of a TaxRecord.
     *
     * @param key       The key to search for (Tax_Year, Tax_return_type, lob_type).
     * @param docLength The document length to be within the lowBound and upperBound.
     * @return The matching TaxRecord, or null if none found.
     */
    public static TaxRecord findTaxRecord(String key, int docLength) {
        List<TaxRecord> records = recordMap.get(key);

        if (records != null) {
            for (TaxRecord record : records) {
                if (docLength >= record.getLowBound() && docLength <= record.getUpperBound()) {
                    return record;
                }
            }
        }

        return null; // No matching record found
    }
}
