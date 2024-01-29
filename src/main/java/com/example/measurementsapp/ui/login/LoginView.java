package com.example.measurementsapp.ui.login;

import com.example.measurementsapp.ui.I18nUtil;
import com.example.measurementsapp.ui.components.CustomLoginForm;
import com.example.measurementsapp.ui.registration.UserRegistrationView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Route("login")
@PageTitle("Logowanie")
@AnonymousAllowed
@CssImport("./styles/style.css")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

  private final CustomLoginForm login = new CustomLoginForm();
  private static final String loginPageTranslation = "{\"form\": {\"title\": \"Logowanie\",\"username\": \"Użytkownik\",\"password\": \"Hasło\",\"submit\": \"Zaloguj się\",\"forgotPassword\": \"Nie pamiętam hasła\"},\"errorMessage\": {\"title\": \"Niepoprawny login lub hasło\",\"message\": \"Sprawdź czy wprowadziłeś poprawny login lub hasło.\"}}\n";

  public LoginView() {
    login.setI18n(I18nUtil.getI18n(loginPageTranslation, LoginI18n.class));
    addClassName("login-view");
    setSizeFull();
    setAlignItems(Alignment.CENTER);
    setJustifyContentMode(JustifyContentMode.CENTER);

    login.setAction("login");
    login.setForgotPasswordButtonVisible(false);
    login.addCustomComponent(createRegisterButton());
    add(login);
    this.addClassName("background");
  }

  @Override
  public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
    if (beforeEnterEvent.getLocation()
        .getQueryParameters()
        .getParameters()
        .containsKey("error")) {
      login.setError(true);
    }
  }

  private Button createRegisterButton() {
    var registerButton = new Button("Zarejestruj się");
    registerButton.setWidth("100%");
    registerButton.getStyle().set("margin-top", "var(--lumo-space-l)");
    registerButton.addClickListener(e -> UI.getCurrent().navigate(UserRegistrationView.class));
    return registerButton;
  }
}
