package com.hsteinmetz.jstove.normalize;

import static org.junit.jupiter.api.Assertions.*;

import com.hsteinmetz.jstove.extract.FieldReader;
import org.junit.jupiter.api.Test;

/**
 * @author Hendrik Steinmetz
 */
class AggregateRatingNormalizerTest extends GenericNormalizerTest {

  AggregateRatingNormalizer normalizer = new AggregateRatingNormalizer(new FieldReader());

  @Test
  void testNull() {
    var result = normalizer.normalize(NODE_FACTORY.nullNode(), PARSE_ISSUE_HANDLER);

    assertTrue(result.isEmpty());
  }

  @Test
  void testValid() {
    var input =
        NODE_FACTORY
            .objectNode()
            .put("ratingValue", 4.5f)
            .put("reviewCount", 100)
            .put("bestRating", 5)
            .put("worstRating", 1);

    var result = normalizer.normalize(input, PARSE_ISSUE_HANDLER);
    assertTrue(result.isPresent());
    assertEquals(5, result.get().bestRating());
    assertEquals(1, result.get().worstRating());
    assertEquals(4.5f, result.get().ratingValue());
    assertEquals(100, result.get().ratingCount());
  }

  @Test
  void testInvalid() {
    var input = NODE_FACTORY.objectNode().put("ratingValue", "invalid");

    var result = normalizer.normalize(input, PARSE_ISSUE_HANDLER);
    assertTrue(result.isPresent());
    assertEquals(0, result.get().ratingValue());
  }
}
