package com.example.measurementsapp.security.model;

import com.example.measurementsapp.measurement.model.Measurement;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationUser {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String username;

  private String password;

  private String firstName;

  private String lastName;

  private boolean enabled;

  @Transient
  private String oldPassword;

  @Transient
  private String newPassword;

  @OneToMany(mappedBy = "applicationUser", cascade = CascadeType.ALL)
  @LazyCollection(LazyCollectionOption.FALSE)
  @ToString.Exclude
  @Builder.Default
  @EqualsAndHashCode.Exclude
  private List<Authority> authorities = new ArrayList<>();
}
