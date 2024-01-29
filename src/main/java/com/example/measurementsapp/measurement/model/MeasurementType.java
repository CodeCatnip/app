package com.example.measurementsapp.measurement.model;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MeasurementType {
  Displacement("Przemieszczenie", "[m]"),
  Speed("Prędkość", "[m/s]"),
  Acceleration("Przyśpieszenie", "[m/s²]");

  private final String translation;
  private final String unit;

  public static MeasurementType getByTranslation(String translation) {
    return Arrays.stream(MeasurementType.values())
        .filter(e -> e.getTranslation().equals(translation))
        .findFirst().orElseThrow(() -> new IllegalArgumentException(
            "Measurement type %s not exists".formatted(translation)));
  }
}
