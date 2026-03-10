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
class RecipeNormalizerTest {
  private static ParseIssueHandler parseIssueHandler;
  private static RecipeNormalizer recipeNormalizer;
  private static ObjectMapper objectMapper;

  @BeforeAll
  static void setUp() {
    parseIssueHandler = new ParseIssueHandler(ParseOptions.defaultOptions());
    recipeNormalizer = new RecipeNormalizer(new FieldReader());
    objectMapper = ObjectMapperFactory.getInstance().getObjectMapper();
  }

  @BeforeEach
  void clearParseIssues() {
    parseIssueHandler.clear();
  }

  @Test
  void testNormalizeFullRecipe() {
    String json =
        """
            {
              "name": "Chocolate Chip Cookies",
              "description": "Delicious homemade chocolate chip cookies.",
              "recipeIngredient": [
                "1 cup butter",
                "1 cup white sugar",
                "2 cups all-purpose flour",
                "1 cup chocolate chips"
              ],
              "recipeInstructions": [
                {
                  "@type": "HowToStep",
                  "text": "Preheat oven to 350 degrees F (175 degrees C)."
                },
                {
                  "@type": "HowToStep",
                  "text": "Cream together the butter and sugar until smooth."
                },
                {
                  "@type": "HowToSection",
                  "name": "Mixing the Dough",
                  "itemListElement": [
                    {
                      "@type": "HowToStep",
                      "text": "Beat in the eggs one at a time."
                    }
                  ]
                },
                {
                  "@type": "HowToStep",
                  "text": "Stir in the flour and chocolate chips."
                },
                {
                  "@type": "HowToStep",
                  "text": "Drop by large spoonfuls onto ungreased pans."
                },
                {
                  "@type": "HowToStep",
                  "text": "Bake for 10 minutes in the preheated oven, or until edges are nicely browned."
                }
              ],
              "recipeYield": "24 cookies",
              "url": "https://example.com/chocolate-chip-cookies",
              "nutrition": {
                "@type": "NutritionInformation",
                "calories": "200 calories",
                "carbohydrateContent": "30 grams",
                "proteinContent": "2 grams",
                "fatContent": "10 grams"
              },
              "author": {
                "@type": "Person",
                "name": "Jane Doe"
              },
              "image": "https://example.com/images/chocolate-chip-cookies.jpg",
              "@type": "Recipe"
            }
            """;
    var node = objectMapper.readTree(json);
    var result = recipeNormalizer.normalize(node, parseIssueHandler);
    assertTrue(result.isPresent());

    var recipe = result.get();
    assertEquals("Chocolate Chip Cookies", recipe.title());
    assertEquals("Delicious homemade chocolate chip cookies.", recipe.description());

    assertEquals(4, recipe.ingredients().size());

    assertEquals(2, recipe.instructionSections().getFirst().steps().size());
    assertEquals("Mixing the Dough", recipe.instructionSections().get(1).name());
    assertEquals(1, recipe.instructionSections().get(1).steps().size());
    assertEquals(3, recipe.instructionSections().getLast().steps().size());

    assertEquals("24 cookies", recipe.yield());
    assertEquals("https://example.com/chocolate-chip-cookies", recipe.sourceUrl());

    assertNotNull(recipe.nutrition());
    assertEquals("200 calories", recipe.nutrition().calories());
    assertEquals("30 grams", recipe.nutrition().carbohydrateContent());
    assertEquals("2 grams", recipe.nutrition().proteinContent());
    assertEquals("10 grams", recipe.nutrition().fatContent());

    assertNotNull(recipe.authors());
    assertEquals(1, recipe.authors().size());
    assertEquals("Jane Doe", recipe.authors().getFirst().name());
  }
}
