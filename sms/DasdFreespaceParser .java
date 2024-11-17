package sms;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DasdFreespaceParser {

    // Class to store specific parsed information from the report description
    public static class ReportInfo {
        private String date;
        private String time;
        private int matchedVolumes;
        private double freePercentage;
        private long freeTracks;

        public ReportInfo(String date, String time, int matchedVolumes, double freePercentage, long freeTracks) {
            this.date = date;
            this.time = time;
            this.matchedVolumes = matchedVolumes;
            this.freePercentage = freePercentage;
            this.freeTracks = freeTracks;
        }

        @Override
        public String toString() {
            return "ReportInfo{" +
                    "date='" + date + '\'' +
                    ", time='" + time + '\'' +
                    ", matchedVolumes=" + matchedVolumes +
                    ", freePercentage=" + freePercentage +
                    ", freeTracks=" + freeTracks +
                    '}';
        }
    }

    public static class ParsedReport {
        private String reportDescription;
        private ReportInfo reportInfo;
        private String columnHeader;
        private List<DasdFreespaceRecord> records;

        public ParsedReport(String reportDescription, ReportInfo reportInfo, String columnHeader, List<DasdFreespaceRecord> records) {
            this.reportDescription = reportDescription;
            this.reportInfo = reportInfo;
            this.columnHeader = columnHeader;
            this.records = records;
        }

        public String getReportDescription() {
            return reportDescription;
        }

        public ReportInfo getReportInfo() {
            return reportInfo;
        }

        public String getColumnHeader() {
            return columnHeader;
        }

        public List<DasdFreespaceRecord> getRecords() {
            return records;
        }

        @Override
        public String toString() {
            return "Report Description:\n" + reportDescription + "\n\n" +
                    "Parsed Report Info:\n" + reportInfo + "\n\n" +
                    "Column Header:\n" + columnHeader + "\n\n" +
                    "Records:\n" + records;
        }
    }
    public static List<DasdFreespaceRecord> sortRecordsWithStream(List<DasdFreespaceRecord> records) {
        // Using Streams to sort by multiple fields: volumeSerial, freeTracks (descending), freeCyls
        return records.stream()
                .sorted(Comparator
                        .comparing(DasdFreespaceRecord::getVolumeSerial)
                        .thenComparing(Comparator.comparingInt(DasdFreespaceRecord::getFreeTracks).reversed()) // Descending
                        .thenComparing(Comparator.comparingInt(DasdFreespaceRecord::getFreeCyls))) // Ascending
                .collect(Collectors.toList());
    }
    public static ParsedReport parseDasdFreespaceReport(List<String> reportLines) {
        StringBuilder reportDescription = new StringBuilder();
        StringBuilder columnHeader = new StringBuilder();
        List<DasdFreespaceRecord> records = new ArrayList<>();

        boolean isHeaderSection = true;
        boolean isDataSection = false;

        for (String line : reportLines) {
            // Stop parsing if we reach "Bottom of List"
            if (line.contains("Bottom of List")) {
                break;
            }

            // Check if we reached the title line indicating the start of the column header
            if (isHeaderSection && line.contains("Volume") && line.contains("Mount")) {
                isHeaderSection = false; // End of report description, move to column header
            }

            // Save report description lines
            if (isHeaderSection) {
                reportDescription.append(line).append("\n");
                continue;
            }

            // Detect the separator line "------" and start parsing data after it
            if (line.startsWith("------")) {
                isDataSection = true;
                continue; // Skip the separator line
            }

            // Save column header content
            if (!isDataSection) {
                columnHeader.append(line).append("\n");
                continue;
            }

            // Split data rows into columns
            String[] columns = line.trim().split("\\s+");
            if (columns.length < 21) continue; // Ensure there are at least 21 columns

            // Parse each column into a record object
            DasdFreespaceRecord record = new DasdFreespaceRecord();
            record.setVolumeSerial(columns[0]);
            record.setMountAttr(columns[1]);
            record.setDeviceType(columns[2]);
            record.setFreeExts(Integer.parseInt(columns[3]));
            record.setFreeTracks(Integer.parseInt(columns[4]));
            record.setFreeCyls(Integer.parseInt(columns[5]));
            record.setLrgFreeTracks(Integer.parseInt(columns[6]));
            record.setLgFreeCyls(Integer.parseInt(columns[7]));
            record.setDasdNmbr(columns[8]);
            record.setVtoc(columns[9]);
            record.setSmsIndx(columns[10]);
            record.setSmsInd(columns[11]);
            record.setDensity(columns[12]);
            record.setStorageGroup(columns[13]);
            record.setVolumeFreePercent(Double.parseDouble(columns[14]));
            record.setDscbFreePercent(Double.parseDouble(columns[15]));
            record.setVirsFreePercent(Double.parseDouble(columns[16]));
            record.setCylsOnVol(Integer.parseInt(columns[17]));
            record.setSmsVolStatus(columns[18]);
            record.setAllocCntr(columns[19]);
            record.setFcDsFlag(columns[20]);

            // Add the record to the result list
            records.add(record);
        }

        // Parse report description to extract specific information
        ReportInfo reportInfo = parseReportInfo(reportDescription.toString());

        return new ParsedReport(reportDescription.toString(), reportInfo, columnHeader.toString(), records);
    }

    private static ReportInfo parseReportInfo(String description) {
        String date = "";
        String time = "";
        int matchedVolumes = 0;
        double freePercentage = 0.0;
        long freeTracks = 0;

        // Regular expressions to match the report information
        Pattern dateTimePattern = Pattern.compile("(\\d{2}/\\d{2}/\\d{4})\\s+\\*\\*.*\\*\\*\\s+(\\d{2}:\\d{2}:\\d{2})");
        Pattern matchedVolumesPattern = Pattern.compile("(\\d+) volumes matched");
        Pattern freePercentagePattern = Pattern.compile("% Free:\\s+(\\d+\\.\\d+)");
        Pattern freeTracksPattern = Pattern.compile("Free Tracks:\\s+([\\d,]+)");

        Matcher matcher = dateTimePattern.matcher(description);
        if (matcher.find()) {
            date = matcher.group(1);
            time = matcher.group(2);
        }

        matcher = matchedVolumesPattern.matcher(description);
        if (matcher.find()) {
            matchedVolumes = Integer.parseInt(matcher.group(1));
        }

        matcher = freePercentagePattern.matcher(description);
        if (matcher.find()) {
            freePercentage = Double.parseDouble(matcher.group(1));
        }

        matcher = freeTracksPattern.matcher(description);
        if (matcher.find()) {
            freeTracks = Long.parseLong(matcher.group(1).replace(",", ""));
        }

        return new ReportInfo(date, time, matchedVolumes, freePercentage, freeTracks);
    }

    public static void main(String[] args) {
        // Example input data (normally you would read this from a file)
        List<String> reportLines = List.of(
            "11/10/2024 ** MVS/QuickRef, Copyright 1989-2023, Chicago Soft, Ltd.** 15:23:11",
            "2008 volumes matched MEF* - % Free: 8.7 - Free Tracks: 256,826,656",
            "----------------------------------------------------------------------",
            "Volume      Mount    Device  Free     Free     Free     Lrg Free   LgFree   DASD  VTOC SMS    SMS",
            "Serial      Attr     Type    Exts     Tracks   Cyls     #Tracks    #Cyls    Nmbr  Indx Ind    Density",
            "Group       VolFree  DSCB    VIR      Free%    CylsVol  SMS Vol    Status   Alloc CNTR   FCDS",
            "-----------------------------------------------------------------------------------------------",
            "MEF60A      PRIVATE  3390    0        0        0        0          0        E5D2  YES   YES   EAV     GMFEF   0.0      99.8     95.3     523110 ENABLE  00000 FCDS",
            "MEF242      PRIVATE  3390    0        0        0        0          0        F27D  YES   YES   EAV     GMFEF   0.0      99.8     95.3     523110 ENABLE  00000 FCDS",
            "MEF133      PRIVATE  3390    0        100.0    95.4     65520      ENABLE  00000 FCDS",
            "Bottom of List"
        );

        // Parse the report
        ParsedReport report = parseDasdFreespaceReport(reportLines);

        // Print report description, parsed info, column header, and data records
        System.out.println(report);
    }
}
