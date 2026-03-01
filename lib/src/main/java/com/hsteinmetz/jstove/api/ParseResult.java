package com.hsteinmetz.jstove.api;

import com.hsteinmetz.jstove.model.Recipe;
import java.util.List;
import java.util.Map;
import tools.jackson.databind.JsonNode;

/**
 * Represents the result of parsing a recipe. This record encapsulates the parsed {@link Recipe},
 * any warnings that were generated during parsing, and a map of unparsed fields that were
 * encountered in the input JSON. The unparsed fields can be used for debugging or for providing
 * feedback to the user about any fields that were not recognized during parsing.
 *
 * @param recipe the parsed recipe
 * @param warnings a list of warnings generated during parsing
 * @param unparsedFields a map of unparsed fields encountered in the input JSON
 */
public record ParseResult(
    Recipe recipe, List<ParseWarning> warnings, Map<String, JsonNode> unparsedFields) {
  public boolean hasWarnings() {
    return warnings != null && !warnings.isEmpty();
  }
}
;
