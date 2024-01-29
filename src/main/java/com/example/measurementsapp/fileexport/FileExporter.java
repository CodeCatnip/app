package com.example.measurementsapp.fileexport;

import com.example.measurementsapp.measurement.model.Measurement;
import java.io.IOException;
import java.util.List;

public interface FileExporter {
  MeasurementFileData export(List<Measurement> measurements) throws IOException;
}
