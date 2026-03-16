package com.hsteinmetz.jstove.normalize;

import static org.junit.jupiter.api.Assertions.*;

import com.hsteinmetz.jstove.api.ParseOptions;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import com.hsteinmetz.jstove.jackson.ObjectMapperFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

/**
 * @author Hendrik Steinmetz
 */
class NutritionNormalizerTest {

  private static ParseIssueHandler parseIssueHandler;
  private static NutritionNormalizer nutritionNormalizer;
  private static ObjectMapper objectMapper;

  @BeforeAll
  static void setUp() {
    parseIssueHandler = new ParseIssueHandler(ParseOptions.defaultOptions());
    nutritionNormalizer = new NutritionNormalizer(new FieldReader());
    objectMapper = ObjectMapperFactory.getInstance().getObjectMapper();
  }

  @BeforeEach
  void clearParseIssues() {
    parseIssueHandler.clear();
  }

  @Test
  void testNormalizeNullInput() {
    assertTrue(
        nutritionNormalizer
            .normalize(
                tools.jackson.databind.node.JsonNodeFactory.instance.nullNode(), parseIssueHandler)
            .isEmpty());
  }

  @Test
  void testNormalizeEmptyObject() {
    assertTrue(
        nutritionNormalizer
            .normalize(
                tools.jackson.databind.node.JsonNodeFactory.instance.objectNode(),
                parseIssueHandler)
            .isPresent());
  }

  @Test
  void testNormalizeValidInput() {
    String json =
        """
            {
                "@type": "NutritionInformation",
                "calories": "210 calories",
                "carbohydrateContent": "30 g",
                "proteinContent": "10 g",
                "fatContent": "5 g",
                "other": "value"
            }
            """;
    var node = objectMapper.readTree(json);
    var result = nutritionNormalizer.normalize(node, parseIssueHandler);
    assertTrue(result.isPresent());

    var nutritionInfo = result.get();
    assertEquals("210 calories", nutritionInfo.calories());
    assertEquals("30 g", nutritionInfo.carbohydrateContent());
    assertEquals("10 g", nutritionInfo.proteinContent());
    assertEquals("5 g", nutritionInfo.fatContent());
    assertEquals(1, nutritionInfo.additionalInfo().size());
    assertEquals("value", nutritionInfo.additionalInfo().get("other"));
  }

  @Test
  void testNormalizeInputWithMissingFields() {
    String json =
        """
              {
                  "@type": "NutritionInformation",
                  "calories": "210 calories"
              }
              """;
    var node = objectMapper.readTree(json);
    var result = nutritionNormalizer.normalize(node, parseIssueHandler);
    assertTrue(result.isPresent());

    var nutritionInfo = result.get();
    assertEquals("210 calories", nutritionInfo.calories());
    assertNull(nutritionInfo.carbohydrateContent());
    assertNull(nutritionInfo.proteinContent());
    assertNull(nutritionInfo.fatContent());
    assertTrue(nutritionInfo.additionalInfo().isEmpty());
  }
}
