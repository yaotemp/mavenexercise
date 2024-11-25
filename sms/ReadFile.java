import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DasdFreespaceParser {

    // Modified ParsedReport class with CSV writing capability
    public static class ParsedReport {
        private String reportDescription;
        private String columnHeader;
        private List<DasdFreespaceRecord> records;
        private String reportInfo;

        public ParsedReport(String reportDescription, String columnHeader, List<DasdFreespaceRecord> records, String reportInfo) {
            this.reportDescription = reportDescription;
            this.columnHeader = columnHeader;
            this.records = records;
            this.reportInfo = reportInfo;
        }

        public String getReportDescription() {
            return reportDescription;
        }

        public String getColumnHeader() {
            return columnHeader;
        }

        public List<DasdFreespaceRecord> getRecords() {
            return records;
        }

        public String getReportInfo() {
            return reportInfo;
        }

        @Override
        public String toString() {
            return "Report Description:\n" + reportDescription + "\n\n" +
                    "Report Info:\n" + reportInfo + "\n\n" +
                    "Column Header:\n" + columnHeader + "\n\n" +
                    "Records:\n" + records;
        }

        // Method to write records to CSV file
        public void writeRecordsToCsv(String csvFilePath) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(csvFilePath))) {
                // Write the report description and reportInfo section
                writer.println("Report Description:");
                writer.println(reportDescription);
                writer.println();
                writer.println("Report Info:");
                writer.println(reportInfo);
                writer.println();

                // Writing the header
                writer.println("VolumeSerial,MountAttr,DeviceType,FreeExts,FreeTracks,FreeCyls,LrgFreeTracks,LgFreeCyls,DasdNmbr,Vtoc,SmsIndx,SmsInd,Density,StorageGroup,VolumeFreePercent,DscbFreePercent,VirsFreePercent,CylsOnVol,SmsVolStatus,AllocCntr,FcDsFlag");

                // Writing each record
                for (DasdFreespaceRecord record : records) {
                    writer.println(
                            String.join(",",
                                    record.getVolumeSerial(),
                                    record.getMountAttr(),
                                    record.getDeviceType(),
                                    String.valueOf(record.getFreeExts()),
                                    String.valueOf(record.getFreeTracks()),
                                    String.valueOf(record.getFreeCyls()),
                                    String.valueOf(record.getLrgFreeTracks()),
                                    String.valueOf(record.getLgFreeCyls()),
                                    record.getDasdNmbr(),
                                    record.getVtoc(),
                                    record.getSmsIndx(),
                                    record.getSmsInd(),
                                    record.getDensity(),
                                    record.getStorageGroup(),
                                    String.valueOf(record.getVolumeFreePercent()),
                                    String.valueOf(record.getDscbFreePercent()),
                                    String.valueOf(record.getVirsFreePercent()),
                                    String.valueOf(record.getCylsOnVol()),
                                    record.getSmsVolStatus(),
                                    record.getAllocCntr(),
                                    record.getFcDsFlag()
                            )
                    );
                }

                System.out.println("Records successfully written to CSV file: " + csvFilePath);
            } catch (IOException e) {
                System.err.println("Error writing to CSV file: " + csvFilePath);
                e.printStackTrace();
            }
        }
    }

    // Method to parse DasdFreespaceReport
    public static ParsedReport parseDasdFreespaceReport(List<String> reportLines) {
        StringBuilder reportDescription = new StringBuilder();
        StringBuilder columnHeader = new StringBuilder();
        StringBuilder reportInfo = new StringBuilder();
        List<DasdFreespaceRecord> records = new ArrayList<>();

        boolean isHeaderSection = true;
        boolean isDataSection = false;

        for (String line : reportLines) {
            // Stop parsing at "Bottom of List"
            if (line.contains("Bottom of List")) {
                break;
            }

            // Check if header row has started
            if (isHeaderSection && line.contains("Volume") && line.contains("Mount")) {
                isHeaderSection = false; // Move from description to header row
            }

            // Save report description part and extract report info
            if (isHeaderSection) {
                reportDescription.append(line).append("\n");
                if (!line.trim().isEmpty()) {
                    reportInfo.append(line).append("\n"); // Add non-empty lines to reportInfo
                }
                continue;
            }

            // After separator line, start processing data
            if (line.startsWith("------")) {
                isDataSection = true;
                continue; // Skip separator line
            }

            // Save header content
            if (!isDataSection) {
                columnHeader.append(line).append("\n");
                continue;
            }

            // Parse data rows
            String[] columns = line.trim().split("\\s+");
            if (columns.length < 21) continue; // Ensure there are enough columns

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

            records.add(record);
        }

        return new ParsedReport(reportDescription.toString(), columnHeader.toString(), records, reportInfo.toString());
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
                "MEF133      PRIVATE  3390    0        0        0        0          0        E5D2  YES   YES   EAV     GMFEF   0.0      99.8     95.3     523110 ENABLE  00000 FCDS",
                "Bottom of List"
        );

        // Parse the report
        ParsedReport report = parseDasdFreespaceReport(reportLines);

        // Write parsed report to CSV file
        String csvFilePath = "parsed_report.csv"; // Specify your CSV file path
        report.writeRecordsToCsv(csvFilePath);
    }
}
