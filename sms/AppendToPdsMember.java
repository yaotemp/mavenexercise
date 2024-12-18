import com.ibm.jzos.ZFile;
import com.ibm.jzos.ZFileException;

public class AppendToPdsMember {
    public static void main(String[] args) {
        String datasetName = "'YOUR.PDS.NAME(MEMBER)'"; // Replace with your PDS and member name
        
        try {
            // Step 1: Read the existing content of the PDS member
            ZFile zfile = new ZFile(datasetName, "rb");
            byte[] buffer = new byte[32760]; // Adjust buffer size as needed
            StringBuilder existingContent = new StringBuilder();
            int bytesRead;
            while ((bytesRead = zfile.read(buffer)) != -1) {
                existingContent.append(new String(buffer, 0, bytesRead));
            }
            zfile.close();

            // Step 2: Append the new record
            String newRecord = "New Record\n";
            existingContent.append(newRecord);

            // Step 3: Write the combined content back to the PDS member
            zfile = new ZFile(datasetName, "wb");
            zfile.write(existingContent.toString().getBytes());
            zfile.close();

            System.out.println("Record appended successfully.");
        } catch (ZFileException e) {
            e.printStackTrace();
        }
    }
}
