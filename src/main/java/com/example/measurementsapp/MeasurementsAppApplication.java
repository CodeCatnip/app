package com.example.measurementsapp;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Theme(themeClass = Lumo.class)
@PWA(name = "Measurements Application", shortName = "Measurements")
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class MeasurementsAppApplication implements AppShellConfigurator {

  public static void main(String[] args) {
    SpringApplication.run(MeasurementsAppApplication.class, args);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }
}
