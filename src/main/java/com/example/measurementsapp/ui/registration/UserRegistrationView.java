package com.example.measurementsapp.ui.registration;

import com.example.measurementsapp.security.model.ApplicationUser;
import com.example.measurementsapp.security.service.ApplicationUserService;
import com.example.measurementsapp.ui.login.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ErrorLevel;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Route(value = "register")
@AnonymousAllowed
@CssImport("./styles/style.css")
public class UserRegistrationView extends VerticalLayout {

  private final ApplicationUserService applicationUserService;
  private final Binder<ApplicationUser> binder = new Binder<>(ApplicationUser.class);

  public UserRegistrationView(ApplicationUserService applicationUserService) {
    this.applicationUserService = applicationUserService;
    this.binder.setBean(ApplicationUser.builder().build());
    this.setSizeFull();
    this.setAlignItems(Alignment.CENTER);
    this.setJustifyContentMode(JustifyContentMode.CENTER);

    createPageTitle();
    createRegistrationForm();
    createSignUpButton();
    this.addClassName("background");
  }

  private void createPageTitle() {
    var h1 = new H1("Zarejestruj się");
    add(h1);
  }

  private void createRegistrationForm() {
    var registrationForm = new VerticalLayout();
    registrationForm.setWidth("30%");

    var username = new TextField("Nazwa użytkownika");
    username.setRequired(true);
    username.setWidth("100%");

    var passwordField = new PasswordField("Hasło");
    passwordField.setRequired(true);
    passwordField.setWidth("100%");

    var firstName = new TextField("Imię");
    firstName.setRequired(true);
    firstName.setWidth("100%");

    var lastName = new TextField("Nazwisko");
    lastName.setRequired(true);
    lastName.setWidth("100%");

    binder.forField(username)
        .withValidator(login -> !applicationUserService.existsByUsername(login),
            "Nazwa użytkownika jest zajęta", ErrorLevel.ERROR)
        .asRequired("Pole wymagane")
        .bind(ApplicationUser::getUsername, ApplicationUser::setUsername);

    binder.forField(passwordField)
        .asRequired("Pole wymagane")
        .bind(ApplicationUser::getPassword, ApplicationUser::setPassword);

    binder.forField(firstName)
        .asRequired("Pole wymagane")
        .bind(ApplicationUser::getFirstName, ApplicationUser::setFirstName);

    binder.forField(lastName)
        .asRequired("Pole wymagane")
        .bind(ApplicationUser::getLastName, ApplicationUser::setLastName);

    registrationForm.add(username, passwordField, firstName, lastName);
    add(registrationForm);
  }

  private void createSignUpButton() {
    var signUpButton = new Button("Zarejestruj się");
    signUpButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    signUpButton.addClickListener(event -> {
      binder.validate();
      if (binder.isValid()) {
        applicationUserService.registerNewUser(binder.getBean());
        UI.getCurrent().navigate(LoginView.class);
      }
    });
    add(signUpButton);
  }
}
