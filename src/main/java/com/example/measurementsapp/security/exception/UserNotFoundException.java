package com.example.measurementsapp.security.exception;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String username) {
    super("User \"%s\" not found".formatted(username));
  }

}
