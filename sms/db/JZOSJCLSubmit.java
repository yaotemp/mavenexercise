import java.io.*;
import java.nio.file.*;
import java.util.*;

public class JZOSJCLSubmit {
    public static void main(String[] args) {
        // 定义模板 JCL 文件相对路径
        String jclTemplatePath = "template.jcl"; // 模板文件名
        String workingDirectory = System.getProperty("user.dir"); // 获取当前工作目录
        String fullJclTemplatePath = Paths.get(workingDirectory, jclTemplatePath).toString(); // 拼接完整路径

        // 动态生成 INSERT SQL
        String generatedSQL = generateInsertSQL(
                "VOL001", "2025-01-26", "12:34:56",
                75.50, 150, 100, 5, 2,
                300, 10, 50, 100, 200, 300, 500
        );

        // 读取模板并替换占位符
        String jclWithSQL = replacePlaceholderInJCL(fullJclTemplatePath, "$$INSERT$$", generatedSQL);

        // 提交替换后的 JCL
        submitJCL(jclWithSQL);
    }

    /**
     * 动态生成 INSERT SQL
     */
    private static String generateInsertSQL(
            String volumeSerial, String recordDate, String recordTime,
            double freePercentage, int freeCylinder, int cylinderThreshold,
            int matchedVolumes, int volumesBelowThreshold, int currentAvailableChunks,
            int next1Day, int next7Days, int next30Days, int next60Days, int next90Days, int next180Days) {

        return String.format(
                "INSERT INTO CYLINDER_CHUNK_DAILY_RECORDS (" +
                "VOLUME_SERIAL, RECORD_DATE, RECORD_TIME, FREE_PERCENTAGE, FREE_CYLINDER, " +
                "CYLINDER_THRESHOLD, MATCHED_VOLUMES, VOLUMES_BELOW_THRESHOLD, " +
                "CURRENT_AVAILABLE_CHUNKS, NEXT_1_DAY, NEXT_7_DAYS, NEXT_30_DAYS, " +
                "NEXT_60_DAYS, NEXT_90_DAYS, NEXT_180_DAYS) " +
                "VALUES ('%s', '%s', '%s', %.2f, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d);",
                volumeSerial, recordDate, recordTime, freePercentage, freeCylinder,
                cylinderThreshold, matchedVolumes, volumesBelowThreshold,
                currentAvailableChunks, next1Day, next7Days, next30Days,
                next60Days, next90Days, next180Days
        );
    }

    /**
     * 读取模板 JCL 并替换占位符
     */
    private static String replacePlaceholderInJCL(String jclTemplatePath, String placeholder, String replacement) {
        StringBuilder jclContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(jclTemplatePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 替换占位符
                jclContent.append(line.replace(placeholder, replacement)).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jclContent.toString();
    }

    /**
     * 提交替换后的 JCL
     */
    private static void submitJCL(String jclContent) {
        try {
            // 使用 ZFile 提交 JCL
            File tempJclFile = File.createTempFile("temp_jcl", ".jcl");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempJclFile))) {
                writer.write(jclContent);
            }

            com.ibm.jzos.ZFile.submit(tempJclFile.getAbsolutePath());
            System.out.println("JCL submitted successfully: " + tempJclFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
