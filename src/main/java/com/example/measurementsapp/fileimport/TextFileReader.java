package com.example.measurementsapp.fileimport;

import com.example.measurementsapp.measurement.MeasurementService;
import com.example.measurementsapp.measurement.model.Measurement;
import com.example.measurementsapp.measurement.model.MeasurementType;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TextFileReader implements MeasurementFileReader {

  private final MeasurementService measurementService;

  @Override
  public void createFromMemoryBuffer(MemoryBuffer buffer) throws IOException {
    try (var inputStream = buffer.getInputStream(); var scanner = new Scanner(inputStream,
        StandardCharsets.UTF_8)) {
      var measurementType = getTypeByTextLine(scanner);
      while (scanner.hasNextLine()) {
        var data = scanner.nextLine().split("\t");
        measurementService.save(Measurement.builder()
            .type(measurementType)
            .timeInSeconds(data[0])
            .measureValueOne(data[1])
            .measureValueTwo(data[2])
            .build());
      }
    }
  }

  private MeasurementType getTypeByTextLine(Scanner scanner) {
    if (scanner.hasNextLine()) {
      var firstLine = scanner.nextLine();
      if (firstLine.contains("[s]") && StringUtils.countMatches(firstLine, "[m]") == 2) {
        return MeasurementType.Displacement;
      }
      if (firstLine.contains("[s]") && StringUtils.countMatches(firstLine, "[m/s2]") == 2) {
        return MeasurementType.Acceleration;
      }
      if (firstLine.contains("[s]") && StringUtils.countMatches(firstLine, "[m/s]") == 2) {
        return MeasurementType.Speed;
      }
      throw new IllegalArgumentException(
          "Measurement unit cannot be determined. First line should contain '[s]'"
              + " and the same unit for every column (supported units: '[m]', '[m/s]', '[m/s2]')");
    } else {
      throw new IllegalArgumentException("Measurement unit cannot be determined. File is Empty.");
    }
  }
}
