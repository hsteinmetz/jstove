package com.hsteinmetz.jstove.internal;

import com.hsteinmetz.jstove.api.FieldNameProvider;
import com.hsteinmetz.jstove.api.FieldType;
import java.util.List;
import java.util.Map;

/**
 * @author Hendrik Steinmetz
 */
public class DefaultFieldNameProvider implements FieldNameProvider {

  private final Map<FieldType, List<String>> fieldNames =
      Map.ofEntries(
          Map.entry(FieldType.TYPE, List.of("@type")),
          Map.entry(FieldType.NAME, List.of("name", "title")),
          Map.entry(FieldType.DESCRIPTION, List.of("description")),
          Map.entry(FieldType.NUTRITION, List.of("nutrition")),
          Map.entry(
              FieldType.RECIPE_INGREDIENT,
              List.of("recipeIngredient", "ingredients", "recipeIngredients")),
          Map.entry(FieldType.RECIPE_INSTRUCTIONS, List.of("recipeInstructions", "instructions")),
          Map.entry(FieldType.IMAGE, List.of("image")),
          Map.entry(FieldType.AUTHOR, List.of("author")),
          Map.entry(FieldType.DATE_PUBLISHED, List.of("datePublished")),
          Map.entry(FieldType.YIELD, List.of("yield", "recipeYield")),
          Map.entry(FieldType.SOURCE_URL, List.of("url", "mainEntityOfPage")),
          Map.entry(FieldType.CATEGORY, List.of("category", "recipeCategory")),
          Map.entry(FieldType.CUISINE, List.of("recipeCuisine", "cuisine")),
          Map.entry(FieldType.PREP_TIME, List.of("prepTime")),
          Map.entry(FieldType.COOK_TIME, List.of("cookTime")),
          Map.entry(FieldType.TOTAL_TIME, List.of("totalTime")));

  @Override
  public List<String> getFieldNamesForType(FieldType type) {
    return this.fieldNames.get(type);
  }
}
