package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import com.hsteinmetz.jstove.model.Ingredient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import tools.jackson.databind.JsonNode;

public class IngredientNormalizer implements GenericNormalizer<List<Ingredient>> {

  private final FieldReader reader;

  public IngredientNormalizer(FieldReader reader) {
    this.reader = reader;
  }

  @Override
  public Optional<List<Ingredient>> normalize(JsonNode input, ParseIssueHandler parseIssueHandler) {
    if (input == null || input.isNull() || input.isMissingNode()) {
      return Optional.empty();
    }
    List<Ingredient> ingredients = new ArrayList<>();

    if (input.isArray()) {
      for (JsonNode node : input) {
        if (node.isString()) {
          ingredients.add(new Ingredient(node.asString()));
        } else {
          parseIssueHandler.warnOrThrow(
              RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
              "recipeIngredient",
              "Unsupported shape for recipeIngredient array element; expected string",
              null);
          return Optional.empty();
        }
      }
    } else if (input.isObject()) {
      var type = reader.readAsText(input, "@type").orElse(null);

      if (type == null) {
        parseIssueHandler.warnOrThrow(
            RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
            "recipeIngredient",
            "Missing @type field for recipeIngredient object",
            null);
        return Optional.empty();
      } else if (!type.equalsIgnoreCase("ItemList")) {
        parseIssueHandler.warnOrThrow(
            RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
            "recipeIngredient",
            "Unsupported @type for recipeIngredient object; expected ItemList",
            null);
        return Optional.empty();
      }

      var itemListElement = input.get("itemListElement");
      if (itemListElement == null) {
        parseIssueHandler.warnOrThrow(
            RecipeParseErrorCode.EMPTY_INGREDIENT_LIST,
            "recipeIngredient",
            "Missing itemListElement field for recipeIngredient object with @type ItemList",
            null);
        return Optional.empty();
      } else if (!itemListElement.isArray()) {
        parseIssueHandler.warnOrThrow(
            RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
            "recipeIngredient",
            "Unsupported shape for itemListElement field; expected array",
            null);
        return Optional.empty();
      }

      itemListElement = itemListElement.asArray();

      if (itemListElement.isEmpty()) {
        parseIssueHandler.warnOrThrow(
            RecipeParseErrorCode.EMPTY_INGREDIENT_LIST,
            "recipeIngredient",
            "Empty itemListElement array for recipeIngredient object with @type ItemList",
            null);
        return Optional.empty();
      }

      for (JsonNode item : itemListElement) {
        if (item.isString()) {
          ingredients.add(new Ingredient(item.asString()));
        } else {
          parseIssueHandler.warnOrThrow(
              RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
              "recipeIngredient",
              "Unsupported shape for itemListElement array element; expected string",
              null);

          return Optional.empty();
        }
      }

      if (ingredients.isEmpty()) {
        parseIssueHandler.warnOrThrow(
            RecipeParseErrorCode.EMPTY_INGREDIENT_LIST,
            "recipeIngredient",
            "No valid ingredients found in itemListElement array for recipeIngredient object with @type ItemList",
            null);
        return Optional.empty();
      }
    } else {
      parseIssueHandler.warnOrThrow(
          RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
          "recipeIngredient",
          "Unsupported shape for recipeIngredient field; expected array or object",
          null);

      return Optional.empty();
    }

    return Optional.of(ingredients);
  }
}
