import com.ibm.jzos.ZFile;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReadFile {

    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
        }

        String inputFileName = args[0];

        // Determine the JAR directory
        String jarDir = getJarDirectory();
        if (jarDir == null) {
            System.out.println("Failed to determine JAR directory.");
            System.exit(1);
        }

        // Generate an output file name with a timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String outputFileName = Paths.get(jarDir, inputFileName + "." + timestamp).toString();

        // Read the file and save content to output file
        readFile(inputFileName, outputFileName);

        // Automatically call the parser on the generated file
        ReportParserUtil.parseSavedReport(outputFileName);
    }

    public static void readFile(String inputFileName, String outputFileName) {
        ZFile dsnFile = null;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            // Open the dataset
            dsnFile = new ZFile(inputFileName, "rb,type=record,noseek");
            int nRead;
            byte[] recBuf = new byte[dsnFile.getLrecl()];

            while ((nRead = dsnFile.read(recBuf)) != -1) {
                String line = new String(recBuf).trim();
                writer.write(line);
                writer.newLine();
            }
            System.out.println("File content saved to: " + outputFileName);
        } catch (Exception e) {
            System.out.println("Unable to read " + inputFileName);
            e.printStackTrace();
        } finally {
            try {
                if (dsnFile != null) {
                    dsnFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String getJarDirectory() {
        try {
            // Get the directory path of the JAR file
            String jarPath = new File(ReadFile.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            return new File(jarPath).getParent();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void usage() {
        System.out.println("ReadFile -- Demonstrates how to read a file sequentially, save it, and parse it.");
        System.out.println("Usage:");
        System.out.println("\tjava com.ibm.jzos.sample.nonvsam.file.ReadFile inputFileName");
        System.out.println("\tinputFileName: The name of the file to read");
        System.exit(0);
    }
}
