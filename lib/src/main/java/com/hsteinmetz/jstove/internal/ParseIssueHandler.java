package com.hsteinmetz.jstove.internal;

import com.hsteinmetz.jstove.api.ParseMode;
import com.hsteinmetz.jstove.api.ParseOptions;
import com.hsteinmetz.jstove.api.ParseWarning;
import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.api.except.RecipeParseException;
import java.util.ArrayList;
import java.util.List;
import tools.jackson.core.JsonPointer;

public final class ParseIssueHandler {
  private final ParseOptions parseOptions;

  private final List<ParseWarning> warnings = new ArrayList<>();

  public ParseIssueHandler(ParseOptions parseOptions) {
    this.parseOptions = parseOptions;
  }

  public void warnOrThrow(
      RecipeParseErrorCode code, String field, String message, JsonPointer pointer) {
    if (parseOptions.mode() == ParseMode.STRICT) {
      throw new RecipeParseException(field + ": " + message + " (" + code + ")");
    } else {
      this.addWarning(code, field, message, pointer);
    }
  }

  private void addWarning(
      RecipeParseErrorCode code, String field, String message, JsonPointer pointer) {
    warnings.add(
        new ParseWarning(code, field, message, pointer == null ? JsonPointer.empty() : pointer));
  }

  private void addWarning(ParseWarning warning) {
    warnings.add(warning);
  }

  public List<ParseWarning> toList() {
    return List.copyOf(warnings);
  }

  public boolean isEmpty() {
    return warnings.isEmpty();
  }

  public ParseOptions getParseOptions() {
    return parseOptions;
  }
}
