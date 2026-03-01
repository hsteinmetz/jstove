package com.hsteinmetz.jstove.internal;

import com.hsteinmetz.jstove.api.ParseMode;
import com.hsteinmetz.jstove.api.ParseOptions;
import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.api.except.RecipeParseException;
import tools.jackson.core.JsonPointer;

public final class Strictness {
  private final ParseOptions parseOptions;

  public Strictness(ParseOptions parseOptions) {
    this.parseOptions = parseOptions;
  }

  public void warnOrThrow(
      WarningCollector warnings,
      RecipeParseErrorCode code,
      String field,
      String message,
      JsonPointer pointer) {
    if (parseOptions.mode() == ParseMode.STRICT) {
      throw new RecipeParseException(field + ": " + message + " (" + code + ")");
    } else {
      warnings.addWarning(code, field, message, pointer);
    }
  }
}
