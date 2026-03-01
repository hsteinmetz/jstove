package com.hsteinmetz.jstove.api;

public record ParseOptions(
    ParseMode mode, boolean keepRawFields, boolean keepSourceNode, boolean preferGraphNode) {
  public static ParseOptions defaultOptions() {
    return new ParseOptions(ParseMode.LENIENT, true, false, true);
  }
}
