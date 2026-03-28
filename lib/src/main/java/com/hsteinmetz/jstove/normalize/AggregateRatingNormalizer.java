package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import com.hsteinmetz.jstove.model.AggregateRating;
import java.util.Optional;
import tools.jackson.databind.JsonNode;

/**
 * @author Hendrik Steinmetz
 */
public class AggregateRatingNormalizer extends GenericNormalizer<AggregateRating> {

  /**
   * Creates a new normalizer backed by the given field reader.
   *
   * @param reader field reader used to access JSON fields
   */
  protected AggregateRatingNormalizer(FieldReader reader) {
    super(reader);
  }

  @Override
  public Optional<AggregateRating> normalize(JsonNode input, ParseIssueHandler parseIssueHandler) {
    if (isBlank(input) || !input.isObject()) {
      return Optional.empty();
    }

    float ratingValue = reader.readNumber(input, "ratingValue").map(Number::floatValue).orElse(0f);
    int ratingCount = reader.readNumber(input, "reviewCount").map(Number::intValue).orElse(0);
    int bestRating = reader.readNumber(input, "bestRating").map(Number::intValue).orElse(0);
    int worstRating = reader.readNumber(input, "worstRating").map(Number::intValue).orElse(0);

    AggregateRating result = new AggregateRating(bestRating, worstRating, ratingValue, ratingCount);

    return Optional.of(result);
  }
}
