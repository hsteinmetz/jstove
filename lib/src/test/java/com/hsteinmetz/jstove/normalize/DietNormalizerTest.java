package com.hsteinmetz.jstove.normalize;

import static org.junit.jupiter.api.Assertions.*;

import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.model.DietType;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.node.JsonNodeFactory;

/**
 * @author Hendrik Steinmetz
 */
class DietNormalizerTest extends GenericNormalizerTest {

  private final DietNormalizer normalizer = new DietNormalizer(new FieldReader());

  @Test
  void testNullAndEmpty() {
    assertEquals(Optional.empty(), normalizer.normalize(Optional.empty(), PARSE_ISSUE_HANDLER));
    assertEquals(
        Optional.empty(),
        normalizer.normalize(JsonNodeFactory.instance.nullNode(), PARSE_ISSUE_HANDLER));
  }

  @Test
  void testStringNormalization() {
    Map<String, DietType> testCases =
        Map.ofEntries(
            Map.entry("DiabeticDiet", DietType.DIABETIC),
            Map.entry("GlutenFreeDiet", DietType.GLUTEN_FREE),
            Map.entry("HalalDiet", DietType.HALAL),
            Map.entry("HinduDiet", DietType.HINDU),
            Map.entry("KosherDiet", DietType.KOSHER),
            Map.entry("LowCalorieDiet", DietType.LOW_CALORIE),
            Map.entry("LowFatDiet", DietType.LOW_FAT),
            Map.entry("LowLactoseDiet", DietType.LOW_LACTOSE),
            Map.entry("LowSaltDiet", DietType.LOW_SALT),
            Map.entry("VeganDiet", DietType.VEGAN),
            Map.entry("VegetarianDiet", DietType.VEGETARIAN));

    testCases
        .keySet()
        .forEach(
            key -> {
              var result =
                  normalizer.normalize(
                      JsonNodeFactory.instance.stringNode(key), PARSE_ISSUE_HANDLER);
              assertFalse(result.isEmpty());
              assertEquals(testCases.get(key), result.get());
            });
  }

  @Test
  void testInvalidDietTypeCausesWarning() {
    var node = JsonNodeFactory.instance.stringNode("test");
    var result = normalizer.normalize(node, PARSE_ISSUE_HANDLER);
    assertTrue(result.isEmpty());
    assertFalse(PARSE_ISSUE_HANDLER.isEmpty());
    assertTrue(
        PARSE_ISSUE_HANDLER.toList().stream()
            .anyMatch(issue -> issue.code().equals(RecipeParseErrorCode.INVALID_DIET_TYPE)));
  }
}
