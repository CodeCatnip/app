package com.example.measurementsapp.ui;

import com.example.measurementsapp.security.service.ApplicationUserService;
import com.example.measurementsapp.ui.profile.ProfileView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@CssImport("./styles/style.css")
public class MainView extends AppLayout {

  public MainView(ApplicationUserService applicationUserService) {

    var authenticatedUser = applicationUserService.getAuthenticatedUser();
    var avatar = new Avatar(
        authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName());
    avatar.getStyle().set("margin-right", "20px");

    var menuBar = new MenuBar();
    menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
    var menuItem = menuBar.addItem(avatar);
    var subMenu = menuItem.getSubMenu();
    subMenu.addItem("Profil", e -> UI.getCurrent().navigate(ProfileView.class));
    subMenu.addItem("Wyloguj", e -> applicationUserService.logout());

    HorizontalLayout header = new HorizontalLayout(menuBar);
    header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
    header.setJustifyContentMode(JustifyContentMode.END);
    header.setWidth("100%");
    header.addClassNames("py-0", "px-m");
    addToNavbar(header);
    addClassName("background");
  }
}
