import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class BlobUtil {

    /**
     * Writes the contents of a Blob from a ResultSet to the provided OutputStream.
     * 
     * @param resultSet The ResultSet, positioned to the correct row.
     * @param columnIndex The index of the column containing the Blob.
     * @param out The OutputStream to write the Blob data to.
     * @throws SQLException If an SQL error occurs.
     * @throws IOException If an IO error occurs.
     */
    public static void writeBlobToOutputStream(ResultSet resultSet, int columnIndex, OutputStream out) throws SQLException, IOException {
        Blob dbBlob = resultSet.getBlob(columnIndex);
        try (InputStream in = dbBlob.getBinaryStream()) {
            byte[] buffer = new byte[1024]; // Use a buffer for efficient reading
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } // InputStream is automatically closed here
    }
}
