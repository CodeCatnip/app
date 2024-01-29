package com.example.measurementsapp.ui.profile;

import com.example.measurementsapp.security.model.ApplicationUser;
import com.example.measurementsapp.security.service.ApplicationUserService;
import com.example.measurementsapp.ui.MainView;
import com.example.measurementsapp.ui.measurement.MeasurementView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ErrorLevel;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

@PermitAll
@PageTitle("Profil")
@Route(value = "profile", layout = MainView.class)
public class ProfileView extends VerticalLayout {

  private final ApplicationUserService applicationUserService;
  private final PasswordEncoder passwordEncoder;
  private final Binder<ApplicationUser> binder = new Binder<>(ApplicationUser.class);

  public ProfileView(ApplicationUserService applicationUserService,
      PasswordEncoder passwordEncoder) {
    this.applicationUserService = applicationUserService;
    this.passwordEncoder = passwordEncoder;
    this.binder.setBean(applicationUserService.getAuthenticatedUser());
    this.setSizeFull();
    this.setAlignItems(Alignment.CENTER);
    this.setJustifyContentMode(JustifyContentMode.CENTER);
    createProfileForm();
  }

  private void createProfileForm() {
    var registrationForm = new VerticalLayout();
    registrationForm.setWidth("30%");

    var username = new TextField("Nazwa użytkownika");
    username.setRequired(false);
    username.setWidth("100%");

    var oldPassword = new PasswordField("Stare hasło");
    oldPassword.setRequired(true);
    oldPassword.setWidth("100%");

    var newPassword = new PasswordField("Nowe hasło");
    newPassword.setRequired(false);
    newPassword.setWidth("100%");

    var confirmPassword = new PasswordField("Powtórz hasło");
    confirmPassword.setRequired(false);
    confirmPassword.setWidth("100%");

    var firstName = new TextField("Imię");
    firstName.setRequired(false);
    firstName.setWidth("100%");

    var lastName = new TextField("Nazwisko");
    lastName.setRequired(false);
    lastName.setWidth("100%");

    binder.forField(username)
        .withValidator(login -> applicationUserService.getAuthenticatedUser().getUsername().equals(login)
                || !applicationUserService.existsByUsername(login),
            "Nazwa użytkownika jest zajęta", ErrorLevel.ERROR)
        .bind(ApplicationUser::getUsername, ApplicationUser::setUsername);

    binder.forField(oldPassword)
        .withValidator(password -> passwordEncoder.matches(password,
            applicationUserService.getAuthenticatedUser().getPassword()), "Błędne hasło")
        .asRequired("Pole wymagane")
            .bind(user -> StringUtils.EMPTY, ApplicationUser::setOldPassword);

    binder.forField(newPassword)
        .withValidator(password -> password.equals(confirmPassword.getValue()), "Niezgodne hasła")
        .bind(user -> StringUtils.EMPTY, ApplicationUser::setNewPassword);

    binder.forField(confirmPassword)
            .withValidator(password -> password.equals(newPassword.getValue()), "Niezgodne hasła")
        .bind(user -> StringUtils.EMPTY, ApplicationUser::setPassword);

    binder.forField(firstName)
        .bind(ApplicationUser::getFirstName, ApplicationUser::setFirstName);

    binder.forField(lastName)
        .bind(ApplicationUser::getLastName, ApplicationUser::setLastName);

    registrationForm.add(username, oldPassword, newPassword, confirmPassword, firstName, lastName);
    add(registrationForm);
    createSubmitButton();
  }

  private void createSubmitButton() {
    var cancel = new Button("Anuluj");
    cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
    cancel.addClickListener(e -> {
      UI.getCurrent().navigate(MeasurementView.class);
    });

    var save = new Button("Zapisz");
    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    save.addClickListener(event -> {
      binder.validate();
      if (binder.isValid()) {
        var userToSave = new ApplicationUser();
        userToSave.setId(binder.getBean().getId());
        userToSave.setLastName(binder.getBean().getLastName());
        userToSave.setFirstName(binder.getBean().getFirstName());

        var currentUser = applicationUserService.getAuthenticatedUser();
        if (StringUtils.isBlank(binder.getBean().getUsername())) {
          userToSave.setUsername(currentUser.getUsername());
        } else {
          userToSave.setUsername(binder.getBean().getUsername());
        }

        if (StringUtils.isBlank(binder.getBean().getPassword())) {
          userToSave.setPassword(currentUser.getPassword());
        } else {
          userToSave.setPassword(binder.getBean().getPassword());
        }
        applicationUserService.registerNewUser(userToSave);
        applicationUserService.logout();
      }
    });
    add(new HorizontalLayout(cancel, save));
  }
}
