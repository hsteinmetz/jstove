package com.hsteinmetz.jstove.api.except;

public class RecipeParseException extends RuntimeException {

  public RecipeParseException(String message) {
    super(message);
  }

  public RecipeParseException(String message, Throwable cause) {
    super(message, cause);
  }
}
