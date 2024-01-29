package com.example.measurementsapp.security;

import com.example.measurementsapp.ui.login.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends VaadinWebSecurity {

  private static final String USER_BY_USERNAME_QUERY =
      "select username, password, enabled from application_user where username = ?";
  private static final String AUTHORITIES_BY_USERNAME_QUERY =
      "select username, role from authority where username = ?";

  private final DataSource dataSource;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/public/**"))
        .permitAll();

    super.configure(http);
    setLoginView(http, LoginView.class);
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    super.configure(web);
  }

  @Bean
  public UserDetailsManager userDetailsService() {
    var jdbcManager = new JdbcUserDetailsManager(dataSource);
    jdbcManager.setUsersByUsernameQuery(USER_BY_USERNAME_QUERY);
    jdbcManager.setAuthoritiesByUsernameQuery(AUTHORITIES_BY_USERNAME_QUERY);
    return jdbcManager;
  }
}
