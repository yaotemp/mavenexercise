import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.util.*;
import org.apache.poi.xddf.usermodel.chart.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ExcelLineChart {

    public static void main(String[] args) throws IOException {
        // Generate mock records
        List<TrackRecord> records = new ArrayList<>();
        records.add(new TrackRecord("VOL001", LocalDate.of(2023, 1, 1), LocalTime.of(10, 30), 75.5, 120, 100, 5, 3, 8, 10, 70, 300, 600, 900, 1800));
        records.add(new TrackRecord("VOL002", LocalDate.of(2023, 1, 2), LocalTime.of(11, 0), 65.2, 110, 90, 6, 2, 9, 20, 80, 320, 620, 920, 1820));
        records.add(new TrackRecord("VOL003", LocalDate.of(2023, 1, 3), LocalTime.of(12, 15), 55.8, 100, 80, 4, 1, 7, 30, 90, 330, 630, 930, 1830));

        // Create an Excel file with a line chart
        createLineChartExcel("TrackRecords.xlsx", records);
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

        // Create a drawing for the chart
        XSSFDrawing drawing = sheet.createDrawingPatriarch();

        // Adjust the position of the chart dynamically based on the record size
        int chartWidth = Math.min(10, records.size()); // Set max width of the chart to 10 columns
        int startColumn = 16; // Starting column for the chart
        int endColumn = startColumn + chartWidth;
        int startRow = 2; // Start at the 3rd row
        int endRow = startRow + 10; // Chart height remains fixed

        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, startColumn, startRow, endColumn, endRow);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Track Usage Over Time");
        chart.setTitleOverlay(false);

        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        // Define data sources for the chart
        XDDFDataSource<String> dates = XDDFDataSourcesFactory.fromStringCellRange(sheet, new CellRangeAddress(1, records.size(), 1, 1));
        XDDFNumericalDataSource<Double> currentAvailableChunks = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, records.size(), 8, 8));
        XDDFNumericalDataSource<Double> next1Day = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, records.size(), 9, 9));
        XDDFNumericalDataSource<Double> next7Days = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, records.size(), 10, 10));
        XDDFNumericalDataSource<Double> next30Days = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, records.size(), 11, 11));
        XDDFNumericalDataSource<Double> next60Days = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, records.size(), 12, 12));
        XDDFNumericalDataSource<Double> next90Days = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, records.size(), 13, 13));
        XDDFNumericalDataSource<Double> next180Days = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, records.size(), 14, 14));

        // Create a line chart
        XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, chart.createCategoryAxis(AxisPosition.BOTTOM), chart.createValueAxis(AxisPosition.LEFT));

        // Add series for each data source
        data.addSeries(dates, currentAvailableChunks).setTitle("Current Available Chunks", null);
        data.addSeries(dates, next1Day).setTitle("Next 1 Day", null);
        data.addSeries(dates, next7Days).setTitle("Next 7 Days", null);
        data.addSeries(dates, next30Days).setTitle("Next 30 Days", null);
        data.addSeries(dates, next60Days).setTitle("Next 60 Days", null);
        data.addSeries(dates, next90Days).setTitle("Next 90 Days", null);
        data.addSeries(dates, next180Days).setTitle("Next 180 Days", null);

        // Plot the chart with the data
        chart.plot(data);

        // Write to an Excel file
        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
        }

        workbook.close();
        System.out.println("Excel file with line chart created: " + fileName);
    }
}
