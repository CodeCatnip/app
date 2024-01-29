package com.example.measurementsapp.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.dom.Element;
import java.util.Arrays;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CustomLoginForm extends LoginForm {

  public void addCustomComponent(Component... pComponents) {
    Element loginFormElement = getElement();

    Element[] elements = Arrays.stream(pComponents).map(Component::getElement)
        .toArray(Element[]::new);
    loginFormElement.appendChild(elements); // to have them in the DOM

    String executeJsForFieldString = "let fields = this.getElementsByTagName($0);" +
        "if(fields && fields[0] && fields[0].id === $1) {" +
        "   fields[0].after($2)" +
        "} else {" +
        "   console.error('could not find field', $0, $1);" +
        "}";

    for (int i = elements.length - 1; i >= 0; i--) {
      loginFormElement.executeJs(executeJsForFieldString, "vaadin-password-field",
          "vaadinLoginPassword", elements[i]);
    }
  }

}
