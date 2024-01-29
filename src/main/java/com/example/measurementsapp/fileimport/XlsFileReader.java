package com.example.measurementsapp.fileimport;

import com.example.measurementsapp.measurement.MeasurementService;
import com.example.measurementsapp.measurement.model.Measurement;
import com.example.measurementsapp.measurement.model.MeasurementType;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class XlsFileReader implements MeasurementFileReader {

  private final MeasurementService measurementService;

  @Override
  public void createFromMemoryBuffer(MemoryBuffer buffer) throws IOException {
    try (var inputStream = buffer.getInputStream()) {
      var workbook = WorkbookFactory.create(inputStream);
      var sheet = workbook.getSheetAt(0);

      var measurementType = getTypeByTextLine(sheet.getRow(0));
      StreamSupport.stream(sheet.spliterator(), false)
          .skip(1)
          .forEach(row -> createMeasurement(row, measurementType));
    }
  }

  private MeasurementType getTypeByTextLine(Row firstRow) {
    if (firstRow == null || firstRow.getLastCellNum() < 3) {
      throw new IllegalArgumentException(
          "Measurement unit cannot be determined: each row in xls file should contains 3 columns");
    }
    var timeCell = firstRow.getCell(0);
    var firstValueCell = firstRow.getCell(1);
    var secondValueCell = firstRow.getCell(2);
    if (timeCell.getCellType() != CellType.STRING || firstValueCell.getCellType() != CellType.STRING
        || secondValueCell.getCellType() != CellType.STRING) {
      throw new IllegalArgumentException(
          "Measurement unit cannot be determined: first row should contains 3 columns with unit type name");
    }
    if (timeCell.getStringCellValue().contains("[s]") && firstValueCell.getStringCellValue()
        .contains("[m]") && secondValueCell.getStringCellValue().contains(
        "[m]")) {
      return MeasurementType.Displacement;
    }
    if (timeCell.getStringCellValue().contains("[s]") && firstValueCell.getStringCellValue()
        .contains("[m/s2]") && secondValueCell.getStringCellValue().contains(
        "[m/s2]")) {
      return MeasurementType.Acceleration;
    }
    if (timeCell.getStringCellValue().contains("[s]") && firstValueCell.getStringCellValue()
        .contains("[m/s]") && secondValueCell.getStringCellValue().contains(
        "[m/s]")) {
      return MeasurementType.Speed;
    }
    throw new IllegalArgumentException(
        "Measurement unit cannot be determined: first line should contain '[s]'"
            + " and the same unit for every column (supported units: '[m]', '[m/s]', '[m/s2]')");
  }

  private void createMeasurement(Row row, MeasurementType measurementType) {
    if (row.getLastCellNum() < 3) {
      throw new IllegalArgumentException(
          "Cannot create measurement: each row in xls file should contains 3 columns.");
    }
    var timeCell = row.getCell(0);
    var measureValueOneCell = row.getCell(1);
    var measureValueTwoCell = row.getCell(2);
    if(timeCell == null || measureValueOneCell == null || measureValueTwoCell == null) {
      return;
    }
    if (timeCell.getCellType() == CellType.BLANK
        && measureValueOneCell.getCellType() == CellType.BLANK
        && measureValueTwoCell.getCellType() == CellType.BLANK) {
      return;
    }
    if (timeCell.getCellType() != CellType.NUMERIC
        || measureValueOneCell.getCellType() != CellType.NUMERIC
        || measureValueTwoCell.getCellType() != CellType.NUMERIC) {
      throw new IllegalArgumentException(
          "Cannot create measurement: data rows should contains numeric values");
    }
    measurementService.save(Measurement.builder()
        .type(measurementType)
        .timeInSeconds(BigDecimal.valueOf(timeCell.getNumericCellValue()).toString())
        .measureValueOne(
            BigDecimal.valueOf(measureValueOneCell.getNumericCellValue()).toString())
        .measureValueTwo(
            BigDecimal.valueOf(measureValueTwoCell.getNumericCellValue()).toString())
        .build());
  }
}
