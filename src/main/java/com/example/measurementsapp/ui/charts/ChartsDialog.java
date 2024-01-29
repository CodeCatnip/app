package com.example.measurementsapp.ui.charts;

import com.example.measurementsapp.measurement.model.Measurement;
import com.example.measurementsapp.measurement.model.MeasurementType;
import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.*;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.builder.ToolbarBuilder;
import com.github.appreciated.apexcharts.config.grid.builder.RowBuilder;
import com.github.appreciated.apexcharts.config.legend.HorizontalAlign;
import com.github.appreciated.apexcharts.config.legend.Position;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.subtitle.Align;
import com.github.appreciated.apexcharts.config.xaxis.builder.TitleBuilder;
import com.github.appreciated.apexcharts.helper.Series;
import com.tambapps.fft4j.FastFouriers;
import com.tambapps.fft4j.Signal;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class ChartsDialog extends Dialog {

    private final List<Measurement> measurements;
    private final FormLayout chartsLayout;

    public ChartsDialog(List<Measurement> measurements) {
        this.measurements = measurements;
        this.chartsLayout = new FormLayout();
        this.chartsLayout.setResponsiveSteps(new ResponsiveStep("0", 2));
        if (measurements.isEmpty()) {
            createEmptyMeasurementsMessage();
        } else {
            this.setHeight("80%");
            this.setWidth("95%");
            createCharts();
        }
    }

    private void createEmptyMeasurementsMessage() {
        var verticalLayout = new VerticalLayout(
                new H2("Wprowadź dane pomiarowe aby wygenerować wykresy"));
        verticalLayout.setSizeFull();
        verticalLayout.setAlignItems(Alignment.CENTER);
        verticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        add(verticalLayout);
    }

    private void createCharts() {
        var measurementType = measurements.get(0).getType();

        var measurementValueOne = measurements.stream()
                .map(Measurement::getValueOneAsBigDecimal)
                .toArray(BigDecimal[]::new);

        var timeValues = measurements.stream()
                .map(Measurement::getTimeAsBigDecimal)
                .toArray(BigDecimal[]::new);

        var measurementValueTwo = measurements.stream()
                .map(Measurement::getValueTwoAsBigDecimal)
                .toArray(BigDecimal[]::new);

        chartsLayout.add(
                createLineChart(measurementType, measurementValueOne, measurementValueTwo, timeValues));
        chartsLayout.add(
                createSteplineChart(measurementType, measurementValueOne, measurementValueTwo, timeValues));
        add(chartsLayout);

        add(createFftLineChart(measurementType, calculateFft(measurementValueOne),
                calculateFft(measurementValueTwo), timeValues));
    }

    private Signal calculateFft(BigDecimal[] data) {
        var valueOneDoubles = Arrays.stream(data)
                .mapToDouble(BigDecimal::doubleValue).toArray();

        return FastFouriers.BASIC.transform(new Signal(valueOneDoubles));
    }

    private ApexCharts createFftLineChart(MeasurementType measurementType,
                                          Signal fftDataOne, Signal fftDataTwo, BigDecimal[] timeValues) {


        var timeSubtract = timeValues[timeValues.length - 1].subtract(timeValues[0]);
        log.info("TIME SUBCTRACTION: {}", timeSubtract);
        var frequencies = calculateFrequency(timeSubtract, timeValues.length);
        log.info("FREQUENCIES: {}", Arrays.toString(frequencies.toArray()));

        var amplitudeOne = calculateAmplitude(fftDataOne);
        var amplitudeTwo = calculateAmplitude(fftDataTwo);

        //rejection of half of the data to the fft chart
        frequencies =frequencies.subList(0, (frequencies.size() / 2)+1);
        amplitudeOne = amplitudeOne.subList(0, (amplitudeOne.size() / 2)+1);
        amplitudeTwo = amplitudeTwo.subList(0, (amplitudeTwo.size() / 2)+1);

        return ApexChartsBuilder.get()
                .withSeries(new Series<>("Amplituda (pomiar 1)", amplitudeOne.toArray()),
                        new Series<>("Amplituda (pomiar 2)", amplitudeTwo.toArray()))
                .withChart(ChartBuilder.get()
                        .withHeight("500")
                        .withType(Type.LINE)
                        .withToolbar(ToolbarBuilder.get()
                                .withShow(false)
                                .build())
                        .build())
                .withColors("#77B6EA", "#545454")
                .withDataLabels(DataLabelsBuilder.get()
                        .withEnabled(false)
                        .build())
                .withStroke(StrokeBuilder.get()
                        .withCurve(Curve.SMOOTH)
                        .build())
                .withTitle(TitleSubtitleBuilder.get()
                        .withText(measurementType.getTranslation() + " - wykres FFT")
                        .withAlign(Align.CENTER)
                        .build())
                .withGrid(GridBuilder.get()
                        .withBorderColor("#e7e7e7")
                        .withRow(RowBuilder.get()
                                .withColors("#f3f3f3", "transparent")
                                .withOpacity(0.5)
                                .build())
                        .build())
                .withMarkers(MarkersBuilder.get()
                        .withSize(1.0, 1.0)
                        .build())
                .withXaxis(XAxisBuilder.get()
                        .withCategories(frequencies.stream().map(BigDecimal::toString).toList())
                        .withTitle(TitleBuilder.get()
                                .withText("Częstotliwość [Hz]")
                                .build())
                        .build())
                .withYaxis(YAxisBuilder.get()
                        .withTitle(com.github.appreciated.apexcharts.config.yaxis.builder.TitleBuilder.get()
                                .withText(measurementType.getTranslation() + " " + measurementType.getUnit())
                                .build())
                        .build())
                .withLegend(LegendBuilder.get()
                        .withPosition(Position.TOP)
                        .withHorizontalAlign(HorizontalAlign.RIGHT)
                        .build())
                .build();
    }

    private List<BigDecimal> calculateFrequency(BigDecimal time, int dataLength) {
        return IntStream.range(1, dataLength + 1)
                .mapToObj(index -> BigDecimal.valueOf((double) index))
                .map(index -> index.divide(time, 2, RoundingMode.HALF_UP))
                .toList();
    }

    private List<Double> calculateAmplitude(Signal fft) {
        var amplitude = new ArrayList<Double>();
        for(var i = 0; i < fft.getLength(); i++) {
            amplitude.add(findModulo(fft.getReAt(i), fft.getImAt(i)));
        }
        log.info("AMPLITUDE: {}", Arrays.toString(amplitude.toArray()));
        return amplitude;
    }

    private double findModulo(double real, double imaginary) {
        return Math.sqrt(real * real + imaginary * imaginary);
    }

    private ApexCharts createSteplineChart(MeasurementType measurementType,
                                           BigDecimal[] measurementValueOne, BigDecimal[] measurementValueTwo, BigDecimal[] timeValues) {
        return ApexChartsBuilder.get()
                .withSeries(new Series<>(
                                measurementType.getTranslation() + " " + measurementType.getUnit() + " - pomiar 1",
                                measurementValueOne),
                        new Series<>(
                                measurementType.getTranslation() + " " + measurementType.getUnit() + " - pomiar 2",
                                measurementValueTwo))
                .withChart(ChartBuilder.get()
                        .withHeight("500")
                        .withType(Type.LINE)
                        .withToolbar(ToolbarBuilder.get()
                                .withShow(false)
                                .build())
                        .build())
                .withColors("#77B6EA", "#545454")
                .withDataLabels(DataLabelsBuilder.get()
                        .withEnabled(false)
                        .build())
                .withStroke(StrokeBuilder.get()
                        .withCurve(Curve.STEPLINE)
                        .build())
                .withTitle(TitleSubtitleBuilder.get()
                        .withText(measurementType.getTranslation() + " " + measurementType.getUnit()
                                + " - wykres schodkowy")
                        .withAlign(Align.CENTER)
                        .build())
                .withGrid(GridBuilder.get()
                        .withBorderColor("#e7e7e7")
                        .withRow(RowBuilder.get()
                                .withColors("#f3f3f3", "transparent")
                                .withOpacity(0.5)
                                .build())
                        .build())
                .withMarkers(MarkersBuilder.get()
                        .withSize(1.0, 1.0)
                        .build())
                .withXaxis(XAxisBuilder.get()
                        .withCategories(Arrays.stream(timeValues).map(BigDecimal::toString).toList())
                        .withTitle(TitleBuilder.get()
                                .withText("Czas [s]")
                                .build())
                        .build())
                .withYaxis(YAxisBuilder.get()
                        .withTitle(com.github.appreciated.apexcharts.config.yaxis.builder.TitleBuilder.get()
                                .withText(measurementType.getTranslation() + " " + measurementType.getUnit())
                                .build())
                        .build())
                .withLegend(LegendBuilder.get()
                        .withPosition(Position.TOP)
                        .withHorizontalAlign(HorizontalAlign.RIGHT)
                        .build())
                .build();
    }

    private ApexCharts createLineChart(MeasurementType measurementType,
                                       BigDecimal[] measurementValueOne, BigDecimal[] measurementValueTwo, BigDecimal[] timeValues) {
        return ApexChartsBuilder.get()
                .withSeries(new Series<>(
                                measurementType.getTranslation() + " " + measurementType.getUnit() + " - pomiar 1",
                                measurementValueOne),
                        new Series<>(
                                measurementType.getTranslation() + " " + measurementType.getUnit() + " - pomiar 2",
                                measurementValueTwo))
                .withChart(ChartBuilder.get()
                        .withHeight("500")
                        .withType(Type.LINE)
                        .withToolbar(ToolbarBuilder.get()
                                .withShow(false)
                                .build())
                        .build())
                .withColors("#77B6EA", "#545454")
                .withDataLabels(DataLabelsBuilder.get()
                        .withEnabled(false)
                        .build())
                .withStroke(StrokeBuilder.get()
                        .withCurve(Curve.SMOOTH)
                        .build())
                .withTitle(TitleSubtitleBuilder.get()
                        .withText(measurementType.getTranslation() + " " + measurementType.getUnit()
                                + " - wykres liniowy")
                        .withAlign(Align.CENTER)
                        .build())
                .withGrid(GridBuilder.get()
                        .withBorderColor("#e7e7e7")
                        .withRow(RowBuilder.get()
                                .withColors("#f3f3f3", "transparent")
                                .withOpacity(0.5)
                                .build())
                        .build())
                .withMarkers(MarkersBuilder.get()
                        .withSize(1.0, 1.0)
                        .build())
                .withXaxis(XAxisBuilder.get()
                        .withCategories(Arrays.stream(timeValues).map(BigDecimal::toString).toList())
                        .withTitle(TitleBuilder.get()
                                .withText("Czas [s]")
                                .build())
                        .build())
                .withYaxis(YAxisBuilder.get()
                        .withTitle(com.github.appreciated.apexcharts.config.yaxis.builder.TitleBuilder.get()
                                .withText(measurementType.getTranslation() + " " + measurementType.getUnit())
                                .build())
                        .build())
                .withLegend(LegendBuilder.get()
                        .withPosition(Position.TOP)
                        .withHorizontalAlign(HorizontalAlign.RIGHT)
                        .build())
                .build();
    }
}
