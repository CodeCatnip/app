package com.example.measurementsapp.fileimport;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SupportedFileType {
  Excel("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsFileReader"),
  Txt("text/plain", "textFileReader");

  private final String mimeType;
  private final String fileReaderType;

  public static SupportedFileType getByMimeType(String mimeType) {
    return Arrays.stream(SupportedFileType.values())
        .filter(e -> e.getMimeType().equals(mimeType))
        .findFirst().orElseThrow(
            () -> new IllegalArgumentException("File %s is not supported".formatted(mimeType)));
  }
}
