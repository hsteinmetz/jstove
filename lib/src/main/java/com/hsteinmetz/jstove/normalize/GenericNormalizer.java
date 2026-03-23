package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import com.hsteinmetz.jstove.normalize.util.NormalizationUtils;
import java.util.Optional;
import tools.jackson.databind.JsonNode;

/**
 * Base class for normalizers that map raw recipe JSON fields into typed values.
 *
 * <p>This class centralizes common utilities used by concrete normalizers, such as:
 *
 * <ul>
 *   <li>Blank/null node checks
 *   <li>Text extraction through {@link FieldReader}
 *   <li>Consistent parse issue reporting
 * </ul>
 *
 * @param <T> the normalized output type produced by a concrete normalizer
 * @author Hendrik Steinmetz
 */
public abstract class GenericNormalizer<T> {

  /** Shared reader used to access recipe fields in a consistent way. */
  protected final FieldReader reader;

  /**
   * Creates a new normalizer backed by the given field reader.
   *
   * @param reader field reader used to access JSON fields
   */
  protected GenericNormalizer(FieldReader reader) {
    this.reader = reader;
  }

  /**
   * Returns whether the provided node should be treated as blank (null/empty).
   *
   * @param node node to inspect
   * @return {@code true} if the node is null or empty, otherwise {@code false}
   */
  protected boolean isBlank(JsonNode node) {
    return NormalizationUtils.isNullOrEmptyNode(node);
  }

  /**
   * Reports a parse issue and returns an empty result for fluent error handling.
   *
   * @param parseIssueHandler handler responsible for warning/throwing
   * @param recipeParseErrorCode error code that identifies the issue type
   * @param field logical field name associated with the issue
   * @param message human-readable issue description
   * @param <R> expected result type for the calling normalization path
   * @return always {@link Optional#empty()}
   */
  protected <R> Optional<R> fail(
      ParseIssueHandler parseIssueHandler,
      RecipeParseErrorCode recipeParseErrorCode,
      String field,
      String message) {
    parseIssueHandler.warnOrThrow(recipeParseErrorCode, field, message, null);
    return Optional.empty();
  }

  /**
   * Reads the given field from the JSON node as text.
   *
   * @param node source JSON node
   * @param field field name to read
   * @return optional containing the field text if present/readable, otherwise empty
   */
  protected Optional<String> text(JsonNode node, String field) {
    return reader.readAsText(node, field);
  }

  /**
   * Normalizes the given JSON input into the target type.
   *
   * @param input source JSON node to normalize
   * @param parseIssueHandler handler for parse warnings/errors
   * @return normalized value when successful, otherwise empty
   */
  public abstract Optional<T> normalize(JsonNode input, ParseIssueHandler parseIssueHandler);

  /**
   * Convenience overload that accepts optional input.
   *
   * <p>If the input is empty, normalization short-circuits and returns {@link Optional#empty()}.
   * Otherwise, delegates to {@link #normalize(JsonNode, ParseIssueHandler)}.
   *
   * @param input optional source JSON node
   * @param parseIssueHandler handler for parse warnings/errors
   * @return normalized value when input is present and normalization succeeds, otherwise empty
   */
  public Optional<T> normalize(Optional<JsonNode> input, ParseIssueHandler parseIssueHandler) {
    if (input.isEmpty()) {
      return Optional.empty();
    }

    return normalize(input.get(), parseIssueHandler);
  }
}
