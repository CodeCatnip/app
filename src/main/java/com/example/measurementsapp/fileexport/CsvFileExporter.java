package com.example.measurementsapp.fileexport;

import com.example.measurementsapp.measurement.model.Measurement;
import com.example.measurementsapp.measurement.model.MeasurementType;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

@Component
public class CsvFileExporter implements FileExporter {

  public static final String BEAN_NAME = "csvFileExporter";


  @Override
  public MeasurementFileData export(List<Measurement> measurements) throws IOException {
    var type = MeasurementType.Acceleration;
    if (!measurements.isEmpty()) {
      type = measurements.get(0).getType();
    }
    var unitString = "Wartosc pomiaru %s".formatted(
        type == MeasurementType.Acceleration ? "[m/s2]" : type.getUnit());
    var headers = new String[]{"Czas [s]", unitString, unitString};

    CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
        .setDelimiter(",")
        .setHeader(headers)
        .build();

    var byteArrayOutputStream = new ByteArrayOutputStream();
    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream));
        CSVPrinter printer = new CSVPrinter(writer, csvFormat)) {
      measurements.forEach(measurement -> {
        try {
          printer.printRecord(measurement.getTimeInSeconds(), measurement.getMeasureValueOne(),
              measurement.getMeasureValueTwo());
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    }
    return new MeasurementFileData("Pomiary.csv", byteArrayOutputStream.toByteArray());
  }
}
