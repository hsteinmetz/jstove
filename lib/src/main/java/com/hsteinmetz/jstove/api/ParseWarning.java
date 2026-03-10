package com.hsteinmetz.jstove.api;

import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import tools.jackson.core.JsonPointer;

/**
 * Represents a warning that occurred during the parsing process. This class is used to encapsulate
 * information about any issues or potential problems encountered while parsing a recipe, such as
 * missing fields, deprecated properties, or other non-critical issues that do not prevent the
 * recipe from being parsed successfully.
 *
 * @param code a unique code identifying the type of warning
 * @param field the name of the field that caused the warning, if applicable
 * @param message a human-readable message describing the warning
 * @param points a JSON pointer indicating the location in the input JSON where the warning occurred
 * @author Hendrik Steinmetz
 */
public record ParseWarning(
    RecipeParseErrorCode code, String field, String message, JsonPointer points) {
  public static ParseWarning of(RecipeParseErrorCode code, String field, String message) {
    return new ParseWarning(code, field, message, JsonPointer.empty());
  }
}
;
