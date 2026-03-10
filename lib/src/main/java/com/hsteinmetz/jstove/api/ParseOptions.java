package com.hsteinmetz.jstove.api;

/**
 * @author Hendrik Steinmetz
 */
public record ParseOptions(ParseMode mode, boolean keepSourceNode) {
  public static ParseOptions defaultOptions() {
    return new ParseOptions(ParseMode.LENIENT, true);
  }
}
