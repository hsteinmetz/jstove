package com.hsteinmetz.jstove.internal;

import com.hsteinmetz.jstove.api.FieldType;
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

  static final Map<FieldType, JsonNodeType> FIELD_TYPE_CHECKS = initFieldTypeChecks();

  static final Map<FieldType, Integer> FIELD_WEIGHTS =
      Map.of(
          FieldType.TYPE,
          10,
          FieldType.NAME,
          5,
          FieldType.DESCRIPTION,
          3,
          FieldType.NUTRITION,
          2,
          FieldType.RECIPE_INGREDIENT,
          5,
          FieldType.RECIPE_INSTRUCTIONS,
          5,
          FieldType.IMAGE,
          1,
          FieldType.AUTHOR,
          1,
          FieldType.DATE_PUBLISHED,
          1);

  private static Map<FieldType, JsonNodeType> initFieldTypeChecks() {
    var checks = new EnumMap<FieldType, JsonNodeType>(FieldType.class);
    for (var fieldType : FieldType.values()) {
      checks.put(fieldType, JsonNodeType.STRING);
    }

    checks.put(FieldType.NUTRITION, JsonNodeType.OBJECT);
    checks.put(FieldType.RECIPE_INGREDIENT, JsonNodeType.ARRAY);
    checks.put(FieldType.RECIPE_INSTRUCTIONS, JsonNodeType.ARRAY);
    checks.put(FieldType.IMAGE, JsonNodeType.ARRAY);
    checks.put(FieldType.AUTHOR, JsonNodeType.OBJECT);

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
