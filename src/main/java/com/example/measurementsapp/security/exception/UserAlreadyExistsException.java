package com.example.measurementsapp.security.exception;

public class UserAlreadyExistsException extends RuntimeException {

  public UserAlreadyExistsException(String username) {
    super("User %s already exists".formatted(username));
  }
}
