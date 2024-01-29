package com.example.measurementsapp.measurement.model;

import com.example.measurementsapp.security.model.ApplicationUser;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String timeInSeconds;

    private String measureValueOne;

    private String measureValueTwo;

    @Enumerated(EnumType.STRING)
    private MeasurementType type;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ApplicationUser applicationUser;

    public String getMeasureValueOneAsString() {
        return switch (type) {
            case Speed -> measureValueOne + " m/s";
            case Acceleration -> measureValueOne + " m/s²";
            case Displacement -> measureValueOne + " m";
        };
    }

    public String getMeasureValueTwoAsString() {
        return switch (type) {
            case Speed -> measureValueTwo + " m/s";
            case Acceleration -> measureValueTwo + " m/s²";
            case Displacement -> measureValueTwo + " m";
        };
    }

    public BigDecimal getValueOneAsBigDecimal() {
        return new BigDecimal(measureValueOne.replace(",", "."));
    }

    public BigDecimal getValueTwoAsBigDecimal() {
        return new BigDecimal(measureValueTwo.replace(",", "."));
    }

    public BigDecimal getTimeAsBigDecimal() {
        return new BigDecimal(timeInSeconds.replace(",", "."));
    }
}
