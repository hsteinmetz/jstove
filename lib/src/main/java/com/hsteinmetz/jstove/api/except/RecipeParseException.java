package com.hsteinmetz.jstove.api.except;

/**
 * @author Hendrik Steinmetz
 */
public class RecipeParseException extends RuntimeException {

  public RecipeParseException(String message) {
    super(message);
  }

  public RecipeParseException(String message, Throwable cause) {
    super(message, cause);
  }
}
