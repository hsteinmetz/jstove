package com.hsteinmetz.jstove.internal;

import com.hsteinmetz.jstove.api.RecipeScorer;
import com.hsteinmetz.jstove.normalize.util.NodeShape;
import java.util.Map;
import tools.jackson.databind.JsonNode;

/**
 * @author Hendrik Steinmetz
 */
public class DefaultRecipeScorer implements RecipeScorer {

  static final Map<FieldName, NodeShape> FIELD_TYPE_CHECKS =
      Map.of(
          FieldName.TYPE,
          NodeShape.STRING,
          //
          FieldName.NAME,
          NodeShape.STRING,
          //
          FieldName.DESCRIPTION,
          NodeShape.STRING,
          //
          FieldName.NUTRITION,
          NodeShape.OBJECT,
          //
          FieldName.RECIPE_INGREDIENT,
          NodeShape.ARRAY,
          //
          FieldName.RECIPE_INSTRUCTIONS,
          NodeShape.ARRAY,
          //
          FieldName.IMAGE,
          NodeShape.ARRAY,
          //
          FieldName.AUTHOR,
          NodeShape.OBJECT,
          //
          FieldName.DATE_PUBLISHED,
          NodeShape.STRING);

  static final Map<FieldName, Integer> FIELD_WEIGHTS =
      Map.of(
          FieldName.TYPE,
          10,
          FieldName.NAME,
          5,
          FieldName.DESCRIPTION,
          3,
          FieldName.NUTRITION,
          2,
          FieldName.RECIPE_INGREDIENT,
          5,
          FieldName.RECIPE_INSTRUCTIONS,
          5,
          FieldName.IMAGE,
          1,
          FieldName.AUTHOR,
          1,
          FieldName.DATE_PUBLISHED,
          1);

  public int score(JsonNode node) {
    int score = 0;

    for (var entry : FIELD_TYPE_CHECKS.entrySet()) {
      var fieldName = entry.getKey();
      var fieldType = entry.getValue();

      if (node.has(fieldName.toString())) {
        var fieldNode = node.get(fieldName.toString());
        boolean typeMatches =
            switch (fieldType) {
              case STRING -> fieldNode.isString();
              case OBJECT -> fieldNode.isObject();
              case ARRAY -> fieldNode.isArray();
              case NUMBER -> fieldNode.isNumber();
              case BOOLEAN -> fieldNode.isBoolean();
              case NULL -> fieldNode.isNull();
              default -> false;
            };

        if (typeMatches) {
          score += FIELD_WEIGHTS.getOrDefault(fieldName, 0);
        }
      }
    }

    return score;
  }
}
