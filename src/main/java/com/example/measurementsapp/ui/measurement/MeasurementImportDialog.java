package com.example.measurementsapp.ui.measurement;

import com.example.measurementsapp.fileimport.MeasurementFileReader;
import com.example.measurementsapp.fileimport.SupportedFileType;
import com.example.measurementsapp.measurement.ImportMeasurementFromFileException;
import com.example.measurementsapp.ui.components.Upload;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import java.io.IOException;
import java.util.Map;

public class MeasurementImportDialog extends Dialog {

  public MeasurementImportDialog(Map<String, MeasurementFileReader> measurementFileReaderMap) {
    var buffer = new MemoryBuffer();
    var upload = new Upload(buffer);

    upload.addSucceededListener(event -> {
      var fileType = SupportedFileType.getByMimeType(event.getMIMEType());
      try {
        measurementFileReaderMap.get(fileType.getFileReaderType()).createFromMemoryBuffer(buffer);
      } catch (IOException e) {
        throw new ImportMeasurementFromFileException(e.getMessage());
      }
      this.close();
    });
    add(upload);
  }
}
