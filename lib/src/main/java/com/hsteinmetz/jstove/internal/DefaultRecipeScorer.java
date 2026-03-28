package com.hsteinmetz.jstove.internal;

import com.hsteinmetz.jstove.api.RecipeField;
import com.hsteinmetz.jstove.api.RecipeScorer;
import com.hsteinmetz.jstove.extract.FieldReader;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.JsonNodeType;

/**
 * @author Hendrik Steinmetz
 */
public class DefaultRecipeScorer implements RecipeScorer {

  private final FieldReader fieldReader;

  static final Map<RecipeField, JsonNodeType> FIELD_TYPE_CHECKS = initFieldTypeChecks();

  static final Map<RecipeField, Integer> FIELD_WEIGHTS =
      Map.of(
          RecipeField.TYPE,
          10,
          RecipeField.NAME,
          5,
          RecipeField.DESCRIPTION,
          3,
          RecipeField.NUTRITION,
          2,
          RecipeField.RECIPE_INGREDIENT,
          5,
          RecipeField.RECIPE_INSTRUCTIONS,
          5,
          RecipeField.IMAGE,
          1,
          RecipeField.AUTHOR,
          1,
          RecipeField.DATE_PUBLISHED,
          1);

  private static Map<RecipeField, JsonNodeType> initFieldTypeChecks() {
    var checks = new EnumMap<RecipeField, JsonNodeType>(RecipeField.class);
    for (var fieldType : RecipeField.values()) {
      checks.put(fieldType, JsonNodeType.STRING);
    }

    checks.put(RecipeField.NUTRITION, JsonNodeType.OBJECT);
    checks.put(RecipeField.RECIPE_INGREDIENT, JsonNodeType.ARRAY);
    checks.put(RecipeField.RECIPE_INSTRUCTIONS, JsonNodeType.ARRAY);
    checks.put(RecipeField.IMAGE, JsonNodeType.ARRAY);
    checks.put(RecipeField.AUTHOR, JsonNodeType.OBJECT);

    return Map.copyOf(checks);
  }

  public DefaultRecipeScorer() {
    this(new FieldReader());
  }

  DefaultRecipeScorer(FieldReader fieldReader) {
    this.fieldReader = Objects.requireNonNull(fieldReader, "fieldReader must not be null");
  }

  public int score(JsonNode node) {
    int score = 0;

    for (var entry : FIELD_TYPE_CHECKS.entrySet()) {
      var fieldType = entry.getKey();
      var expectedShape = entry.getValue();

      var fieldNode = fieldReader.readFirst(node, fieldType.getFieldNames());
      boolean typeMatches =
          fieldNode.map(jsonNode -> expectedShape == jsonNode.getNodeType()).orElse(false);

      if (typeMatches) {
        score += FIELD_WEIGHTS.getOrDefault(fieldType, 0);
      }
    }

    return score;
  }
}
