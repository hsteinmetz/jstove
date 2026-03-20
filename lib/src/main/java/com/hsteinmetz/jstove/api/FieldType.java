package com.hsteinmetz.jstove.api;

import java.util.List;
import lombok.Getter;

/**
 * Maps common recipe fields to their possible field names in the input JSON. Each enum constant
 * represents a specific recipe field and is associated with a list of potential field names that
 * may be used in the input data to represent that field. This allows for flexible parsing of recipe
 * data, accommodating variations in field naming across different sources.
 *
 * @author Hendrik Steinmetz
 */
public enum FieldType {
  TYPE("@type"),
  NAME("name", "title"),
  DESCRIPTION("description"),
  NUTRITION("nutrition"),
  RECIPE_INGREDIENT("recipeIngredient", "ingredients", "recipeIngredients"),
  RECIPE_INSTRUCTIONS("recipeInstructions", "instructions"),
  IMAGE("image"),
  AUTHOR("author"),
  DATE_PUBLISHED("datePublished"),
  DATE_CREATED("dateCreated"),
  DATE_UPDATED("dateUpdated"),
  YIELD("yield", "recipeYield"),
  SOURCE_URL("url", "mainEntityOfPage"),
  CATEGORY("category", "recipeCategory"),
  CUISINE("recipeCuisine", "cuisine"),
  PREP_TIME("prepTime"),
  COOK_TIME("cookTime"),
  TOTAL_TIME("totalTime"),
  PERFORM_TIME("performTime"),
  KEYWORDS("keywords"),
  COOKING_METHOD("cookingMethod");

  @Getter private final List<String> fieldNames;

  FieldType(String... names) {
    this.fieldNames = List.of(names);
  }
}
