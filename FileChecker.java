import java.nio.file.Files;
import java.nio.file.Paths;

public class FileChecker {

    public static void main(String[] args) {
        if (args.length == 1 && (args[0].equals("-h") || args[0].equals("--help"))) {
            printUsage();
            return;
        }

        String[] files = initializeAndCheckFiles(args);
        if (files == null) {
            printUsage();
            return;
        }

        System.out.println("All files exist and are accessible:");
        for (String file : files) {
            System.out.println(file);
        }

        // Additional operations with files can be done here
    }

    /**
     * Prints the usage instructions for the program.
     */
    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java FileChecker [configFile1 configFile2 configFile3 configFile4]");
        System.out.println("Arguments:");
        System.out.println("  configFile1 - Path to the first configuration file (optional)");
        System.out.println("  configFile2 - Path to the second configuration file (optional)");
        System.out.println("  configFile3 - Path to the third configuration file (optional)");
        System.out.println("  configFile4 - Path to the fourth configuration file (optional)");
        System.out.println("If no arguments are provided, default paths are used:");
        System.out.println("  defaultPath1, defaultPath2, defaultPath3, defaultPath4");
        System.out.println("Provide all four paths or none to use defaults.");
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
        for (String file : files) {
            if (!Files.exists(Paths.get(file))) {
                System.out.println("Error: " + file + " does not exist.");
                return null;
            }
        }

        return files;
    }
}
