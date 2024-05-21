import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class DataFileProcessor {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter the folder path: ");
        String folderPath = scanner.nextLine();

        System.out.print("Please enter the number of threads: ");
        int threadSize = scanner.nextInt();

        Path path = Paths.get(folderPath);

        // Check if the folder exists, if not, create it
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                System.out.println("The folder did not exist, so it was created.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to create the directory. Exiting.");
                return;
            }
        }

        try {
            List<Path> dataFiles = listDataFiles(path);
            Collections.sort(dataFiles);

            if (dataFiles.size() < threadSize) {
                System.out.printf("Error: There are not enough .data files for the specified thread size (%d).%n", threadSize);
                System.out.printf("Number of .data files found: %d%n", dataFiles.size());
                return;
            }

            System.out.println("Number of .data files: " + dataFiles.size());
            System.out.println("Sorted .data files:");
            for (Path file : dataFiles) {
                System.out.println(file.getFileName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        scanner.close();
    }

    public static List<Path> listDataFiles(Path path) throws IOException {
        List<Path> dataFiles = new ArrayList<>();

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".data")) {
                    dataFiles.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        return dataFiles;
    }
}
