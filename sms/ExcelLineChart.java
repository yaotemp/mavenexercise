import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.util.*;
import org.apache.poi.xddf.usermodel.chart.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ExcelLineChart {

    public static void main(String[] args) throws IOException {
        List<TrackRecord> records = List.of(
            new TrackRecord(LocalDate.of(2023, 1, 1), 10, 70, 300, 600, 900, 1800),
            new TrackRecord(LocalDate.of(2023, 1, 2), 20, 80, 320, 620, 920, 1820),
            new TrackRecord(LocalDate.of(2023, 1, 3), 30, 90, 330, 630, 930, 1830)
        );

        createLineChartExcel("TrackRecords.xlsx", records);
    }

    public static void createLineChartExcel(String fileName, List<TrackRecord> records) throws IOException {
        // Create a workbook and sheet
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Track Records");

        // Write data headers
        String[] headers = {"Date", "Next 1 Day", "Next 7 Days", "Next 30 Days", "Next 60 Days", "Next 90 Days", "Next 180 Days"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // Write TrackRecord data
        for (int i = 0; i < records.size(); i++) {
            TrackRecord record = records.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(record.getDate().toString());
            row.createCell(1).setCellValue(record.getNext1Day());
            row.createCell(2).setCellValue(record.getNext7Days());
            row.createCell(3).setCellValue(record.getNext30Days());
            row.createCell(4).setCellValue(record.getNext60Days());
            row.createCell(5).setCellValue(record.getNext90Days());
            row.createCell(6).setCellValue(record.getNext180Days());
        }

        // Create a drawing for the chart
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, records.size() + 2, 10, records.size() + 20);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Track Usage Over Time");
        chart.setTitleOverlay(false);

        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        // Define data sources for the chart
        XDDFDataSource<String> dates = XDDFDataSourcesFactory.fromStringCellRange(sheet, new CellRangeAddress(1, records.size(), 0, 0));
        XDDFNumericalDataSource<Double> next1Day = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, records.size(), 1, 1));
        XDDFNumericalDataSource<Double> next7Days = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, records.size(), 2, 2));
        XDDFNumericalDataSource<Double> next30Days = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, records.size(), 3, 3));

        // Create a line chart
        XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, chart.createCategoryAxis(AxisPosition.BOTTOM), chart.createValueAxis(AxisPosition.LEFT));
        data.addSeries(dates, next1Day).setTitle("Next 1 Day", null);
        data.addSeries(dates, next7Days).setTitle("Next 7 Days", null);
        data.addSeries(dates, next30Days).setTitle("Next 30 Days", null);

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
