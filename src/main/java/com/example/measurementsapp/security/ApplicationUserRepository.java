package com.example.measurementsapp.security;

import com.example.measurementsapp.security.model.ApplicationUser;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, UUID> {

  boolean existsByUsername(String username);
  Optional<ApplicationUser> findByUsername(String username);
}
