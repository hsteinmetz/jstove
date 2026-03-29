package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.api.RecipeField;
import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import com.hsteinmetz.jstove.model.CostInfo;
import java.util.Optional;
import tools.jackson.databind.JsonNode;

/**
 * @author Hendrik Steinmetz
 */
public class EstimatedCostNormalizer extends GenericNormalizer<CostInfo> {
  /**
   * Creates a new normalizer backed by the given field reader.
   *
   * @param reader field reader used to access JSON fields
   */
  protected EstimatedCostNormalizer(FieldReader reader) {
    super(reader);
  }

  @Override
  public Optional<CostInfo> normalize(JsonNode input, ParseIssueHandler parseIssueHandler) {
    if (isBlank(input)) {
      return Optional.empty();
    }

    return switch (input.getNodeType()) {
      case STRING ->
          Optional.of(
              new CostInfo(
                  null, 0L, 0L, input.asString())); // TODO decide if default value 0 is appropriate
      case OBJECT -> {
        // TODO decide if default value 0 is appropriate
        var currencyOpt =
            reader.readAsText(input, RecipeField.CURRENCY.getFieldNames()).orElse(null);
        var value =
            reader
                .readNumber(input, RecipeField.VALUE.getFieldNames())
                .map(Number::longValue)
                .orElse(0L);
        var minValue =
            reader
                .readNumber(input, RecipeField.MIN_VALUE.getFieldNames())
                .map(Number::longValue)
                .orElse(0L);
        var maxValue =
            reader
                .readNumber(input, RecipeField.MAX_VALUE.getFieldNames())
                .map(Number::longValue)
                .orElse(0L);

        var costInfo = new CostInfo(currencyOpt, minValue, maxValue, value);
        yield Optional.of(costInfo);
      }
      default -> {
        fail(
            parseIssueHandler,
            RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
            "Estimated cost field has unsupported shape: " + input.getNodeType(),
            null);
        yield Optional.empty();
      }
    };
  }
}
