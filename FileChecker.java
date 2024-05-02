import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileChecker {

    public static void main(String[] args) {
        String configFile1, configFile2, configFile3, configFile4;

        if (args.length == 4) {
            configFile1 = args[0];
            configFile2 = args[1];
            configFile3 = args[2];
            configFile4 = args[3];
        } else if (args.length == 0) {
            configFile1 = "defaultPath1";
            configFile2 = "defaultPath2";
            configFile3 = "defaultPath3";
            configFile4 = "defaultPath4";
        } else {
            System.out.println("Please provide either four file paths or none.");
            return;
        }

        if (!Files.exists(Paths.get(configFile1)) ||
            !Files.exists(Paths.get(configFile2)) ||
            !Files.exists(Paths.get(configFile3)) ||
            !Files.exists(Paths.get(configFile4))) {
            System.out.println("One or more files do not exist.");
            return;
        }

        System.out.println("All files exist and are accessible.");
    }
}
