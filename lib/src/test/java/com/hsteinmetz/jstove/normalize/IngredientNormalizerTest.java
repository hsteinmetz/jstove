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
import tools.jackson.databind.node.JsonNodeFactory;

/**
 * @author Hendrik Steinmetz
 */
class IngredientNormalizerTest {

  private static ParseIssueHandler parseIssueHandler;
  private static IngredientNormalizer ingredientNormalizer;
  private static ObjectMapper objectMapper;

  @BeforeAll
  static void setup() {
    parseIssueHandler = new ParseIssueHandler(ParseOptions.defaultOptions());
    ingredientNormalizer = new IngredientNormalizer(new FieldReader());
    objectMapper = ObjectMapperFactory.getInstance().getObjectMapper();
  }

  @BeforeEach
  void clearParseIssues() {
    parseIssueHandler.clear();
  }

  @Test
  void testNormalizeNullInput() {
    assertTrue(ingredientNormalizer.normalize(null, parseIssueHandler).isEmpty());
    assertTrue(
        ingredientNormalizer
            .normalize(JsonNodeFactory.instance.nullNode(), parseIssueHandler)
            .isEmpty());
  }

  @Test
  void testNormalizeTextArray() {
    String json =
        """
        [
          "1 cup flour",
          "2 eggs",
          "1/2 cup sugar"
        ]
        """;
    var input = objectMapper.readTree(json);
    var result = ingredientNormalizer.normalize(input, parseIssueHandler);
    assertTrue(result.isPresent());
    var ingredients = result.get();
    assertEquals(3, ingredients.size());
    assertEquals("1 cup flour", ingredients.getFirst().displayText());
    assertEquals("2 eggs", ingredients.get(1).displayText());
    assertEquals("1/2 cup sugar", ingredients.get(2).displayText());
  }

  @Test
  void testNormalizeItemListObject() {
    String json =
        """
        {
          "@type": "ItemList",
          "itemListElement": [
            "1 cup flour",
            "2 eggs",
            "1/2 cup sugar"
          ]
        }
        """;
    var input = objectMapper.readTree(json);
    var result = ingredientNormalizer.normalize(input, parseIssueHandler);
    assertTrue(result.isPresent());
    var ingredients = result.get();
    assertEquals(3, ingredients.size());
    assertEquals("1 cup flour", ingredients.getFirst().displayText());
    assertEquals("2 eggs", ingredients.get(1).displayText());
    assertEquals("1/2 cup sugar", ingredients.get(2).displayText());
  }

  @Test
  void testNormalizeItemListElementWithStringValue() {
    String json =
        """
        {
          "@type": "ItemList",
          "itemListElement": "1 cup flour"
        }
        """;
    var input = objectMapper.readTree(json);
    var result = ingredientNormalizer.normalize(input, parseIssueHandler);
    assertTrue(result.isEmpty());
    assertFalse(parseIssueHandler.toList().isEmpty());
  }

  @Test
  void testNormalizeItemListObjectWithMissingType() {
    String json =
        """
            {
            "itemListElement": [
                "1 cup flour",
                "2 eggs",
                "1/2 cup sugar"
            ]
            }
            """;
    var input = objectMapper.readTree(json);
    var result = ingredientNormalizer.normalize(input, parseIssueHandler);
    assertTrue(result.isEmpty());
    assertFalse(parseIssueHandler.toList().isEmpty());
  }

  @Test
  void testNormalizeItemListObjectWithUnsupportedType() {
    String json =
        """
            {
            "@type": "NotAnItemList",
            "itemListElement": [
                "1 cup flour",
                "2 eggs",
                "1/2 cup sugar"
            ]
            }
            """;
    var input = objectMapper.readTree(json);
    var result = ingredientNormalizer.normalize(input, parseIssueHandler);
    assertTrue(result.isEmpty());
    assertFalse(parseIssueHandler.toList().isEmpty());
  }

  @Test
  void testNormalizeItemListObjectWithMissingItemListElement() {
    String json =
        """
                {
                "@type": "ItemList"
                }
                """;
    var input = objectMapper.readTree(json);
    var result = ingredientNormalizer.normalize(input, parseIssueHandler);
    assertTrue(result.isEmpty());
    assertFalse(parseIssueHandler.toList().isEmpty());
  }

  @Test
  void testNormalizeEmptyItemList() {
    String json =
        """
                {
                "@type": "ItemList",
                "itemListElement": []
                }
                """;
    var input = objectMapper.readTree(json);
    var result = ingredientNormalizer.normalize(input, parseIssueHandler);
    assertTrue(result.isEmpty());
    assertFalse(parseIssueHandler.toList().isEmpty());
  }

  @Test
  void testNormalizeUnsupportedShape() {
    String json =
        """
            {
            "unexpectedField": "unexpectedValue"
            }
            """;
    var input = objectMapper.readTree(json);
    var result = ingredientNormalizer.normalize(input, parseIssueHandler);
    assertTrue(result.isEmpty());
    assertFalse(parseIssueHandler.toList().isEmpty());
  }

  @Test
  void testNormalizeUnsupportedShapeInArray() {
    String json =
        """
                [
                "1 cup flour",
                {
                    "unexpectedField": "unexpectedValue"
                },
                "1/2 cup sugar"
                ]
                """;
    var input = objectMapper.readTree(json);
    var result = ingredientNormalizer.normalize(input, parseIssueHandler);
    assertTrue(result.isEmpty());
    assertFalse(parseIssueHandler.toList().isEmpty());
  }
}
