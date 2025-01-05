import com.ibm.jzos.ZFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.util.*;
import org.apache.poi.xddf.usermodel.chart.*;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ExcelLineChartFromZFile {

    public static void main(String[] args) throws IOException {
        // Mainframe ZFile dataset path
        String datasetPath = "//'MY.MAINFRAME.DATASET'"; // Replace with your dataset name

        // Parse the ZFile dataset into TrackRecord objects
        List<TrackRecord> records = parseZFileDataset(datasetPath);

        // Create an Excel file with a line chart
        createLineChartExcel("TrackRecords.xlsx", records);
    }

    public static List<TrackRecord> parseZFileDataset(String datasetPath) throws IOException {
        List<TrackRecord> records = new ArrayList<>();

        // Open the ZFile for reading
        ZFile zfile = new ZFile(datasetPath, "rb,type=record");
        try {
            byte[] recordBuffer = new byte[zfile.getLrecl()];
            while (zfile.read(recordBuffer) != -1) {
                String record = new String(recordBuffer).trim();

                if (record.length() < 73) {
                    System.err.println("Skipping invalid record: " + record);
                    continue;
                }

                // Parse fixed-width fields
                String volSer = record.substring(0, 6).trim();
                LocalDate date = LocalDate.parse(record.substring(6, 16).trim());
                LocalTime time = LocalTime.parse(record.substring(16, 24).trim());
                double freePercentage = Double.parseDouble(record.substring(24, 29).trim());
                int freeCylinder = Integer.parseInt(record.substring(29, 33).trim());
                int cylinderThreshold = Integer.parseInt(record.substring(33, 37).trim());
                int matchedVolumes = Integer.parseInt(record.substring(37, 41).trim());
                int volumesBelowThreshold = Integer.parseInt(record.substring(41, 45).trim());
                int currentAvailableChunks = Integer.parseInt(record.substring(45, 49).trim());
                int next1Day = Integer.parseInt(record.substring(49, 53).trim());
                int next7Days = Integer.parseInt(record.substring(53, 57).trim());
                int next30Days = Integer.parseInt(record.substring(57, 61).trim());
                int next60Days = Integer.parseInt(record.substring(61, 65).trim());
                int next90Days = Integer.parseInt(record.substring(65, 69).trim());
                int next180Days = Integer.parseInt(record.substring(69, 73).trim());

                // Create TrackRecord object
                records.add(new TrackRecord(volSer, date, time, freePercentage, freeCylinder,
                        cylinderThreshold, matchedVolumes, volumesBelowThreshold, currentAvailableChunks,
                        next1Day, next7Days, next30Days, next60Days, next90Days, next180Days));
            }
        } finally {
            zfile.close();
        }

        return records;
    }

    public static void createLineChartExcel(String fileName, List<TrackRecord> records) throws IOException {
        // Create a workbook and sheet
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Track Records");

        // Write data headers
        String[] headers = {"Volume Serial", "Date", "Time", "Free Percentage", "Free Cylinder",
                "Cylinder Threshold", "Matched Volumes", "Volumes Below Threshold",
                "Current Available Chunks", "Next 1 Day", "Next 7 Days",
                "Next 30 Days", "Next 60 Days", "Next 90 Days", "Next 180 Days"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // Write TrackRecord data
        for (int i = 0; i < records.size(); i++) {
            TrackRecord record = records.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(record.getVolSer());
            row.createCell(1).setCellValue(record.getDate().toString());
            row.createCell(2).setCellValue(record.getTime().toString());
            row.createCell(3).setCellValue(record.getFreePercentage());
            row.createCell(4).setCellValue(record.getFreeCylinder());
            row.createCell(5).setCellValue(record.getCylinderThreshold());
            row.createCell(6).setCellValue(record.getMatchedVolumes());
            row.createCell(7).setCellValue(record.getVolumesBelowThreshold());
            row.createCell(8).setCellValue(record.getCurrentAvailableChunks());
            row.createCell(9).setCellValue(record.getNext1Day());
            row.createCell(10).setCellValue(record.getNext7Days());
            row.createCell(11).setCellValue(record.getNext30Days());
            row.createCell(12).setCellValue(record.getNext60Days());
            row.createCell(13).setCellValue(record.getNext90Days());
            row.createCell(14).setCellValue(record.getNext180Days());
        }

        // Chart creation logic remains unchanged
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        int chartWidth = Math.min(10, records.size());
        int startColumn = 16;
        int endColumn = startColumn + chartWidth;
        int startRow = 2;
        int endRow = startRow + 10;
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, startColumn, startRow, endColumn, endRow);
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Track Usage Over Time");
        chart.setTitleOverlay(false);

        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        XDDFDataSource<String> dates = XDDFDataSourcesFactory.fromStringCellRange(sheet, new CellRangeAddress(1, records.size(), 1, 1));
        XDDFNumericalDataSource<Double> currentAvailableChunks = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, records.size(), 8, 8));
        XDDFNumericalDataSource<Double> next1Day = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, records.size(), 9, 9));
        XDDFNumericalDataSource<Double> next7Days = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, records.size(), 10, 10));
        XDDFNumericalDataSource<Double> next30Days = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, records.size(), 11, 11));
        XDDFNumericalDataSource<Double> next60Days = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, records.size(), 12, 12));
        XDDFNumericalDataSource<Double> next90Days = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, records.size(), 13, 13));
        XDDFNumericalDataSource<Double> next180Days = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, records.size(), 14, 14));

        XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, chart.createCategoryAxis(AxisPosition.BOTTOM), chart.createValueAxis(AxisPosition.LEFT));
        data.addSeries(dates, currentAvailableChunks).setTitle("Current Available Chunks", null);
        data.addSeries(dates, next1Day).setTitle("Next 1 Day", null);
        data.addSeries(dates, next7Days).setTitle("Next 7 Days", null);
        data.addSeries(dates, next30Days).setTitle("Next 30 Days", null);
        data.addSeries(dates, next60Days).setTitle("Next 60 Days", null);
        data.addSeries(dates, next90Days).setTitle("Next 90 Days", null);
        data.addSeries(dates, next180Days).setTitle("Next 180 Days", null);
        chart.plot(data);

        // Save Excel file
        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
        }
        workbook.close();
        System.out.println("Excel file with line chart created: " + fileName);
    }
}
