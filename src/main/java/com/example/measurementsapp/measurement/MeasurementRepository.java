package com.example.measurementsapp.measurement;

import com.example.measurementsapp.measurement.model.Measurement;
import com.example.measurementsapp.security.model.ApplicationUser;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, UUID> {
  List<Measurement> findAllByApplicationUser(ApplicationUser applicationUser);
  Optional<Measurement> findByIdAndAndApplicationUser(UUID id, ApplicationUser applicationUser);
  boolean existsByIdAndApplicationUser(UUID id, ApplicationUser applicationUser);
}
