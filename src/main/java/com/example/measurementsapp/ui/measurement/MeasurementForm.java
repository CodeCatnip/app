package com.example.measurementsapp.ui.measurement;

import com.example.measurementsapp.measurement.MeasurementService;
import com.example.measurementsapp.measurement.model.Measurement;
import com.example.measurementsapp.measurement.model.MeasurementType;
import com.example.measurementsapp.ui.MainView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@PermitAll
@PageTitle("Dodawanie pomiaru")
@Route(value = "add-measurement", layout = MainView.class)
@RequiredArgsConstructor
public class MeasurementForm extends VerticalLayout implements HasUrlParameter<String> {

  private Measurement measurement;
  private final Binder<Measurement> binder = new Binder<>(Measurement.class);
  private final MeasurementService measurementService;

  @Override
  public void setParameter(BeforeEvent beforeEvent, String measurementId) {
    if (measurementId.equals("new")) {
      measurement = Measurement.builder()
          .type(MeasurementType.Displacement)
          .build();
    } else {
      measurement = measurementService.findById(UUID.fromString(measurementId));
    }
    binder.setBean(measurement);
    createForm();
  }

  private void createForm() {
    this.setSizeFull();
    this.setAlignItems(Alignment.CENTER);
    this.setJustifyContentMode(JustifyContentMode.CENTER);

    createRegistrationForm();
    createSubmitButton();
  }

  private void createRegistrationForm() {
    var form = new VerticalLayout();
    form.setWidth("30%");

    var timeInSecondsInput = new TextField("Czas [s]");
    timeInSecondsInput.setRequired(true);
    timeInSecondsInput.setWidth("100%");

    var measurementType = new ComboBox<String>();
    measurementType.setItems(Arrays.stream(MeasurementType.values()).map(
        MeasurementType::getTranslation).toList());
    measurementType.setRequired(true);
    measurementType.setWidth("100%");

    var measurementValueOne = new TextField("Wartość pomiaru [m]");
    measurementValueOne.setRequired(true);
    measurementValueOne.setWidth("100%");

    var measurementValueTwo = new TextField("Wartość pomiaru [m]");
    measurementValueTwo.setRequired(true);
    measurementValueTwo.setWidth("100%");

    binder.forField(timeInSecondsInput)
        .withValidator(value -> {
          try {
            new BigDecimal(value.replace(",", "."));
            return true;
          } catch (NumberFormatException e) {
            return false;
          }
        }, "Zły format danych")
        .bind(Measurement::getTimeInSeconds, Measurement::setTimeInSeconds);

    binder.forField(measurementValueOne)
        .withValidator(value -> {
          try {
            new BigDecimal(value.replace(",", "."));
            return true;
          } catch (NumberFormatException e) {
            return false;
          }
        }, "Zły format danych")
        .bind(Measurement::getMeasureValueOne,
            Measurement::setMeasureValueOne);

    binder.forField(measurementValueTwo)
        .withValidator(value -> {
          try {
            new BigDecimal(value.replace(",", "."));
            return true;
          } catch (NumberFormatException e) {
            return false;
          }
        }, "Zły format danych")
        .bind(Measurement::getMeasureValueTwo,
            Measurement::setMeasureValueTwo);

    binder.bind(measurementType,
        measurement -> measurement.getType().getTranslation(),
        (measurement, translation) -> measurement.setType(
            MeasurementType.getByTranslation(translation)));

    measurementType.addValueChangeListener(e -> {
      if (MeasurementType.getByTranslation(e.getValue()) == MeasurementType.Displacement) {
        measurementValueOne.setLabel("Wartość pomiaru [m]");
        measurementValueTwo.setLabel("Wartość pomiaru [m]");
      } else if (MeasurementType.getByTranslation(e.getValue()) == MeasurementType.Speed) {
        measurementValueOne.setLabel("Wartość pomiaru [m/s]");
        measurementValueTwo.setLabel("Wartość pomiaru [m/s]");
      } else if (MeasurementType.getByTranslation(e.getValue()) == MeasurementType.Acceleration) {
        measurementValueOne.setLabel("Wartość pomiaru [m/s²]");
        measurementValueTwo.setLabel("Wartość pomiaru [m/s²]");
      }
    });

    form.add(timeInSecondsInput, measurementValueOne, measurementValueTwo, measurementType);
    add(form);
  }

  private void createSubmitButton() {
    var save = new Button("Zapisz");
    var cancel = new Button("Anuluj");
    cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
    cancel.addClickListener(e -> {
      UI.getCurrent().navigate(MeasurementView.class);
    });

    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    save.addClickListener(event -> {
      binder.validate();
      if (binder.isValid()) {
        measurementService.save(binder.getBean());
        UI.getCurrent().navigate(MeasurementView.class);
      }
    });
    add(new HorizontalLayout(save, cancel));
  }
}
