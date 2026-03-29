package com.hsteinmetz.jstove.normalize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hsteinmetz.jstove.extract.FieldReader;
import org.junit.jupiter.api.Test;

/**
 * @author Hendrik Steinmetz
 */
public class EstimatedCostNormalizerTest extends GenericNormalizerTest {
  private final EstimatedCostNormalizer normalizer = new EstimatedCostNormalizer(new FieldReader());

  @Test
  void testNull() {
    var result = normalizer.normalize(NODE_FACTORY.nullNode(), PARSE_ISSUE_HANDLER);
    assertTrue(result.isEmpty());
  }

  @Test
  void testString() {
    var result = normalizer.normalize(NODE_FACTORY.stringNode("5 USD"), PARSE_ISSUE_HANDLER);
    assertTrue(result.isPresent());
    assertEquals("5 USD", result.get().value());
  }

  @Test
  void testObject() {
    String json =
        """
         {
            "currency": "USD",
            "value": 5,
            "minValue": 3,
            "maxValue": 7
          }
         """;
    var result = normalizer.normalize(MAPPER.readTree(json), PARSE_ISSUE_HANDLER);
    assertTrue(result.isPresent());

    var costInfo = result.get();
    assertEquals("USD", costInfo.currency());
    assertEquals(3L, costInfo.minValue());
    assertEquals(7L, costInfo.maxValue());
    assertEquals(5L, costInfo.value());
  }
}
