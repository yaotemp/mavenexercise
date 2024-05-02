import java.nio.file.Files;
import java.nio.file.Paths;

public class FileChecker {

    public static void main(String[] args) {
        String[] files = initializeAndCheckFiles(args);
        if (files == null) {
            System.out.println("Initialization failed: Either an incorrect number of arguments were provided or one or more files do not exist.");
            return;
        }

        System.out.println("All files exist and are accessible:");
        for (String file : files) {
            System.out.println(file);
        }

        // Additional operations with files can be done here
    }

    /**
     * Initializes file paths based on provided arguments and checks their existence.
     * @param args Command line arguments.
     * @return An array of file paths if all files exist and the correct number of arguments are provided, null otherwise.
     */
    private static String[] initializeAndCheckFiles(String[] args) {
        String configFile1 = "defaultPath1";
        String configFile2 = "defaultPath2";
        String configFile3 = "defaultPath3";
        String configFile4 = "defaultPath4";

        if (args.length == 4) {
            configFile1 = args[0];
            configFile2 = args[1];
            configFile3 = args[2];
            configFile4 = args[3];
        } else if (args.length != 0) {
            return null;
        }

        String[] files = {configFile1, configFile2, configFile3, configFile4};
        if (!checkFilesExist(files)) {
            return null;
        }

        return files;
    }

    /**
     * Checks if all provided file paths exist.
     * @param paths Array of file paths to check.
     * @return true if all files exist, false otherwise.
     */
    private static boolean checkFilesExist(String[] paths) {
        for (String path : paths) {
            if (!Files.exists(Paths.get(path))) {
                return false;
            }
        }
        return true;
    }
}
