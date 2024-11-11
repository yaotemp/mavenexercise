package sms;

import java.util.ArrayList;
import java.util.List;

public class DasdFreespaceParser {

    public static class ParsedReport {
        private String reportDescription;
        private String columnHeader;
        private List<DasdFreespaceRecord> records;

        public ParsedReport(String reportDescription, String columnHeader, List<DasdFreespaceRecord> records) {
            this.reportDescription = reportDescription;
            this.columnHeader = columnHeader;
            this.records = records;
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

        @Override
        public String toString() {
            return "Report Description:\n" + reportDescription + "\n\n" +
                    "Column Header:\n" + columnHeader + "\n\n" +
                    "Records:\n" + records;
        }
    }

    public static ParsedReport parseDasdFreespaceReport(List<String> reportLines) {
        StringBuilder reportDescription = new StringBuilder();
        StringBuilder columnHeader = new StringBuilder();
        List<DasdFreespaceRecord> records = new ArrayList<>();

        boolean isHeaderSection = true;
        boolean isDataSection = false;

        for (String line : reportLines) {
            // 检查是否到达“Bottom of List”行，停止解析
            if (line.contains("Bottom of List")) {
                break;
            }

            // 检查是否到达标题行
            if (isHeaderSection && line.contains("Volume") && line.contains("Mount")) {
                isHeaderSection = false; // 报告说明部分结束，进入标题行
            }

            // 保存报告说明部分
            if (isHeaderSection) {
                reportDescription.append(line).append("\n");
                continue;
            }

            // 检测分隔行“------”，之后进入数据部分
            if (line.startsWith("------")) {
                isDataSection = true;
                continue; // 跳过分隔行
            }

            // 保存标题行内容，包括列标题和分隔行
            if (!isDataSection) {
                columnHeader.append(line).append("\n");
                continue;
            }

            // 分割数据行，根据空格拆分各个字段
            String[] columns = line.trim().split("\\s+");
            if (columns.length < 21) continue; // 确保有至少21列数据

            // 解析每一列，并创建一个新的记录对象
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

            // 将记录添加到结果列表
            records.add(record);
        }

        return new ParsedReport(reportDescription.toString(), columnHeader.toString(), records);
    }

    public static void main(String[] args) {
        // 示例输入数据（通常您可以从文件中读取此数据）
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

        // 解析报告
        ParsedReport report = parseDasdFreespaceReport(reportLines);

        // 打印报告说明、标题行和数据记录
        System.out.println(report);
    }
}

