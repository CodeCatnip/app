package com.example.measurementsapp.fileimport;

import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import java.io.IOException;

public interface MeasurementFileReader {
  void createFromMemoryBuffer(MemoryBuffer buffer) throws IOException;
}
