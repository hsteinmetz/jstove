package com.hsteinmetz.jstove.internal;

import com.hsteinmetz.jstove.api.ParseMode;
import com.hsteinmetz.jstove.api.ParseOptions;
import com.hsteinmetz.jstove.api.ParseWarning;
import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.api.except.RecipeParseException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import tools.jackson.core.JsonPointer;

/**
 * Handles parsing issues by either throwing exceptions or collecting warnings based on the
 * configured {@link ParseOptions}. This class provides methods like {@link
 * ParseIssueHandler#warnOrThrow(RecipeParseErrorCode, String, String, JsonPointer)} to report
 * parsing issues and manage the collected warnings.
 *
 * @author Hendrik Steinmetz
 */
public final class ParseIssueHandler {

  // TODO make configurable via env var, file or ParseOptions
  private final int MAX_WARNINGS = 100;
  @Getter private final ParseOptions parseOptions;

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

  public void clear() {
    warnings.clear();
  }

  private void addWarning(
      RecipeParseErrorCode code, String field, String message, JsonPointer pointer) {
    if (warnings.size() >= MAX_WARNINGS) {
      throw new IllegalStateException("Maximum number of warnings (" + MAX_WARNINGS + ") exceeded");
    }

    warnings.add(
        new ParseWarning(code, field, message, pointer == null ? JsonPointer.empty() : pointer));
  }

  public List<ParseWarning> toList() {
    return List.copyOf(warnings);
  }

  public boolean isEmpty() {
    return warnings.isEmpty();
  }
}
