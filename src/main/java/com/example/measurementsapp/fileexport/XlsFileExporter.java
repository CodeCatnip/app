package com.example.measurementsapp.fileexport;

import com.example.measurementsapp.measurement.model.Measurement;
import com.example.measurementsapp.measurement.model.MeasurementType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class XlsFileExporter implements FileExporter {

  public static final String BEAN_NAME = "xlsFileExporter";

  @Override
  public MeasurementFileData export(List<Measurement> measurements) throws IOException {
    var type = MeasurementType.Acceleration;
    if (!measurements.isEmpty()) {
      type = measurements.get(0).getType();
    }

    var workbook = new XSSFWorkbook();
    var sheet = workbook.createSheet("Pomiary");
    sheet.setColumnWidth(0, 8000);
    sheet.setColumnWidth(1, 8000);
    sheet.setColumnWidth(2, 8000);

    createHeader(workbook, sheet, type);
    var rowNumber = 1;
    for (var measurement : measurements) {
      var style = workbook.createCellStyle();
      style.setWrapText(true);

      var row = sheet.createRow(rowNumber++);
      var cell = row.createCell(0);
      cell.setCellValue(measurement.getTimeInSeconds());
      cell.setCellStyle(style);

      cell = row.createCell(1);
      cell.setCellValue(measurement.getMeasureValueOne());
      cell.setCellStyle(style);

      cell = row.createCell(2);
      cell.setCellValue(measurement.getMeasureValueTwo());
      cell.setCellStyle(style);
    }
    var byteArrayOutputStream = new ByteArrayOutputStream();
    workbook.write(byteArrayOutputStream);
    workbook.close();

    return new MeasurementFileData("Pomiary.xlsx", byteArrayOutputStream.toByteArray());
  }

  private Row createHeader(Workbook workbook, Sheet sheet, MeasurementType type) {
    var header = sheet.createRow(0);

    var headerStyle = workbook.createCellStyle();
    var font = ((XSSFWorkbook) workbook).createFont();
    font.setFontName("Arial");
    font.setFontHeightInPoints((short) 14);
    font.setBold(false);
    headerStyle.setFont(font);

    var headerCell = header.createCell(0);
    headerCell.setCellValue("Czas [s]");
    headerCell.setCellStyle(headerStyle);

    headerCell = header.createCell(1);
    headerCell.setCellValue("Wartość Pomiaru " + type.getUnit());
    headerCell.setCellStyle(headerStyle);

    headerCell = header.createCell(2);
    headerCell.setCellValue("Wartość Pomiaru " + type.getUnit());
    headerCell.setCellStyle(headerStyle);
    return header;
  }
}
