package com.example.measurementsapp.security.service;

import com.example.measurementsapp.security.exception.UserAlreadyExistsException;
import com.example.measurementsapp.security.exception.UserNotFoundException;
import com.example.measurementsapp.security.model.ApplicationUser;
import com.example.measurementsapp.security.model.Authority;
import com.example.measurementsapp.security.model.Role;
import com.example.measurementsapp.security.ApplicationUserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationUserService {

  private final ApplicationUserRepository applicationUserRepository;
  private final SecurityService securityService;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public ApplicationUser registerNewUser(ApplicationUser applicationUser) {
    if (applicationUser.getId() == null && applicationUserRepository.existsByUsername(applicationUser.getUsername())) {
      throw new UserAlreadyExistsException(applicationUser.getUsername());
    }
    if(applicationUserRepository.existsByUsername(applicationUser.getUsername()) && !applicationUserRepository.findByUsername(
        applicationUser.getUsername()).get().getId().equals(applicationUser.getId())) {
      throw new UserAlreadyExistsException(applicationUser.getUsername());
    }
    applicationUser.setPassword(passwordEncoder.encode(applicationUser.getPassword()));
    applicationUser.setEnabled(true);
    applicationUser.setAuthorities(List.of(Authority.builder()
        .applicationUser(applicationUser)
        .username(applicationUser.getUsername())
        .role(Role.USER)
        .build()));
    return applicationUserRepository.save(applicationUser);
  }

  public boolean existsByUsername(String username) {
    return applicationUserRepository.existsByUsername(username);
  }

  @Transactional
  public ApplicationUser getAuthenticatedUser() {
    var userDetails = securityService.getAuthenticatedUser();
    return applicationUserRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new UserNotFoundException(userDetails.getUsername()));
  }

  public void logout() {
    securityService.logout();
  }
}
