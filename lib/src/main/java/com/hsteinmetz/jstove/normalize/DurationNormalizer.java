package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import java.time.Duration;
import java.util.Optional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.JsonNodeType;

/**
 * @author Hendrik Steinmetz
 */
public class DurationNormalizer extends GenericNormalizer<Duration> {

  public DurationNormalizer(FieldReader fieldReader) {
    super(fieldReader);
  }

  @Override
  public Optional<Duration> normalize(JsonNode input, ParseIssueHandler parseIssueHandler) {
    if (isBlank(input)) return Optional.empty();

    if (input.getNodeType().equals(JsonNodeType.STRING)) {
      return parseDuration(input, parseIssueHandler);
    }

    parseIssueHandler.warnOrThrow(
        RecipeParseErrorCode.DURATION_INVALID,
        "@root",
        "Duration node is not a string; using zero duration",
        null);
    return Optional.empty();
  }

  private Optional<Duration> parseDuration(JsonNode input, ParseIssueHandler parseIssueHandler) {
    try {
      Duration duration = Duration.parse(input.asString());
      return Optional.of(duration);
    } catch (Exception e) {
      parseIssueHandler.warnOrThrow(
          RecipeParseErrorCode.DURATION_INVALID,
          "@root",
          "Invalid duration format: " + input.asString(),
          null);
      return Optional.empty();
    }
  }
}
