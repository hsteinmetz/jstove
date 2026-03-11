package com.hsteinmetz.jstove.internal;

import com.hsteinmetz.jstove.api.RecipeScorer;
import java.util.Map;
import tools.jackson.databind.JsonNode;

/**
 * @author Hendrik Steinmetz
 */
public class DefaultRecipeScorer implements RecipeScorer {

  static final Map<String, FieldType> FIELD_TYPE_CHECKS =
      Map.of(
          "@type",
          FieldType.STRING,
          "name",
          FieldType.STRING,
          "description",
          FieldType.STRING,
          "nutrition",
          FieldType.OBJECT,
          "recipeIngredient",
          FieldType.ARRAY,
          "recipeInstructions",
          FieldType.ARRAY,
          "image",
          FieldType.ARRAY,
          "author",
          FieldType.OBJECT,
          "datePublished",
          FieldType.STRING);

  static final Map<String, Integer> FIELD_WEIGHTS =
      Map.of(
          "@type",
          10,
          "name",
          5,
          "description",
          3,
          "nutrition",
          2,
          "recipeIngredient",
          5,
          "recipeInstructions",
          5,
          "image",
          1,
          "author",
          1,
          "datePublished",
          1);

  public int score(JsonNode node) {
    int score = 0;

    for (var entry : FIELD_TYPE_CHECKS.entrySet()) {
      var fieldName = entry.getKey();
      var fieldType = entry.getValue();

      if (node.has(fieldName)) {
        var fieldNode = node.get(fieldName);
        boolean typeMatches =
            switch (fieldType) {
              case STRING -> fieldNode.isString();
              case OBJECT -> fieldNode.isObject();
              case ARRAY -> fieldNode.isArray();
              case NUMBER -> fieldNode.isNumber();
            };

        if (typeMatches) {
          score += FIELD_WEIGHTS.getOrDefault(fieldName, 0);
        }
      }
    }

    return score;
  }

  private enum FieldType {
    STRING,
    OBJECT,
    ARRAY,
    NUMBER
  }
}
