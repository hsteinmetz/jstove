package com.hsteinmetz.jstove.internal;

import com.hsteinmetz.jstove.api.FieldType;
import com.hsteinmetz.jstove.api.RecipeScorer;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.normalize.util.NodeShape;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import tools.jackson.databind.JsonNode;

/**
 * @author Hendrik Steinmetz
 */
public class DefaultRecipeScorer implements RecipeScorer {

  private final FieldReader fieldReader;

  static final Map<FieldType, NodeShape> FIELD_TYPE_CHECKS = initFieldTypeChecks();

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

  private static Map<FieldType, NodeShape> initFieldTypeChecks() {
    var checks = new EnumMap<FieldType, NodeShape>(FieldType.class);
    for (var fieldType : FieldType.values()) {
      checks.put(fieldType, NodeShape.STRING);
    }

    checks.put(FieldType.NUTRITION, NodeShape.OBJECT);
    checks.put(FieldType.RECIPE_INGREDIENT, NodeShape.ARRAY);
    checks.put(FieldType.RECIPE_INSTRUCTIONS, NodeShape.ARRAY);
    checks.put(FieldType.IMAGE, NodeShape.ARRAY);
    checks.put(FieldType.AUTHOR, NodeShape.OBJECT);

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
          fieldNode.map(jsonNode -> expectedShape == NodeShape.of(jsonNode)).orElse(false);

      if (typeMatches) {
        score += FIELD_WEIGHTS.getOrDefault(fieldType, 0);
      }
    }

    return score;
  }
}
