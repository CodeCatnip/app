package com.example.measurementsapp.measurement;

import com.example.measurementsapp.measurement.model.Measurement;
import com.example.measurementsapp.security.service.ApplicationUserService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeasurementService {

  private final ApplicationUserService applicationUserService;
  private final MeasurementRepository measurementRepository;

  @Transactional
  public Measurement save(Measurement measurement) {
    measurement.setApplicationUser(applicationUserService.getAuthenticatedUser());
    return measurementRepository.save(measurement);
  }

  @Transactional
  public List<Measurement> findAll() {
    return measurementRepository.findAllByApplicationUser(
        applicationUserService.getAuthenticatedUser());
  }

  @Transactional
  public Measurement findById(UUID id) {
    return measurementRepository.findByIdAndAndApplicationUser(id,
        applicationUserService.getAuthenticatedUser()).orElseThrow(() -> new IllegalCallerException(
        "Measurement %s not exists or not belongs to current authenticated user".formatted(id)));
  }

  @Transactional
  public void delete(UUID id) {
    if (!measurementRepository.existsByIdAndApplicationUser(id,
        applicationUserService.getAuthenticatedUser())) {
      throw new IllegalCallerException(
          "Measurement %s not exists or not belongs to current authenticated user".formatted(id));
    }
    measurementRepository.deleteById(id);
  }

  @Transactional
  public void deleteAll() {
    var measurements = measurementRepository.findAllByApplicationUser(
        applicationUserService.getAuthenticatedUser());
    measurementRepository.deleteAll(measurements);
  }
}
