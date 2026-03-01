package com.hsteinmetz.jstove.internal;

import com.hsteinmetz.jstove.api.ParseWarning;
import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import java.util.ArrayList;
import java.util.List;
import tools.jackson.core.JsonPointer;

public final class WarningCollector {
  private final List<ParseWarning> warnings = new ArrayList<>();

  public void addWarning(RecipeParseErrorCode code, String field, String message, JsonPointer pointer) {
    warnings.add(
        new ParseWarning(code, field, message, pointer == null ? JsonPointer.empty() : pointer));
  }

  public void addWarning(ParseWarning warning) {
    warnings.add(warning);
  }

  public List<ParseWarning> toList() {
    return List.copyOf(warnings);
  }

  public boolean isEmpty() {
    return warnings.isEmpty();
  }
}
