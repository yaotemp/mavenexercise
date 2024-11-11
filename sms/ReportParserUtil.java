package sms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ReportParserUtil {

    public static void parseSavedReport(String filePath) {
        List<String> reportLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                reportLines.add(line);
            }

            // 调用解析方法处理内容
            DasdFreespaceParser.ParsedReport report = DasdFreespaceParser.parseDasdFreespaceReport(reportLines);
            System.out.println(report);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String outputFileName = "path/to/your/output/file.txt";
        
        // 调用解析方法
        parseSavedReport(outputFileName);
    }
}
