package com.example.measurementsapp.security.service;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {

  private static final String LOGOUT_SUCCESS_URL = "/";

  public void logout() {
    UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
    logoutHandler.logout(
        VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
        null);
  }

  public UserDetails getAuthenticatedUser() {
    SecurityContext context = SecurityContextHolder.getContext();
    Object principal = context.getAuthentication().getPrincipal();
    if (principal instanceof UserDetails) {
      return (UserDetails) context.getAuthentication().getPrincipal();
    }
    return null;
  }
}
