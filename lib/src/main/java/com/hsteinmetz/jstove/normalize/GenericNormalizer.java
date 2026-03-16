package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import com.hsteinmetz.jstove.normalize.util.NormalizationUtils;
import java.util.Optional;
import tools.jackson.databind.JsonNode;

/**
 * @author Hendrik Steinmetz
 */
public abstract class GenericNormalizer<T> {

  protected final FieldReader reader;

  protected GenericNormalizer(FieldReader reader) {
    this.reader = reader;
  }

  protected boolean isBlank(JsonNode node) {
    return NormalizationUtils.isNullOrEmptyNode(node);
  }

  protected <R> Optional<R> fail(
      ParseIssueHandler parseIssueHandler,
      RecipeParseErrorCode recipeParseErrorCode,
      String field,
      String message) {
    parseIssueHandler.warnOrThrow(recipeParseErrorCode, field, message, null);
    return Optional.empty();
  }

  protected Optional<String> text(JsonNode node, String field) {
    return reader.readAsText(node, field);
  }

  public abstract Optional<T> normalize(JsonNode input, ParseIssueHandler parseIssueHandler);

  public Optional<T> normalize(Optional<JsonNode> input, ParseIssueHandler parseIssueHandler) {
    if (input.isEmpty()) {
      return Optional.empty();
    }

    return normalize(input.get(), parseIssueHandler);
  }
}
