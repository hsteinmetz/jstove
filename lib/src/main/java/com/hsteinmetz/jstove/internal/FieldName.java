package com.hsteinmetz.jstove.internal;

/**
 * @author Hendrik Steinmetz
 */
public enum FieldName {
  TYPE,
  NAME,
  DESCRIPTION,
  NUTRITION,
  RECIPE_INGREDIENT,
  RECIPE_INSTRUCTIONS,
  IMAGE,
  AUTHOR,
  DATE_PUBLISHED;

  public static FieldName fromString(String fieldName) {
    return switch (fieldName) {
      case "@type" -> TYPE;
      case "name" -> NAME;
      case "description" -> DESCRIPTION;
      case "nutrition" -> NUTRITION;
      case "recipeIngredient" -> RECIPE_INGREDIENT;
      case "recipeInstructions" -> RECIPE_INSTRUCTIONS;
      case "image" -> IMAGE;
      case "author" -> AUTHOR;
      case "datePublished" -> DATE_PUBLISHED;
      default -> throw new IllegalArgumentException("Unknown field name: " + fieldName);
    };
  }

  public String toString() {
    return switch (this) {
      case TYPE -> "@type";
      case NAME -> "name";
      case DESCRIPTION -> "description";
      case NUTRITION -> "nutrition";
      case RECIPE_INGREDIENT -> "recipeIngredient";
      case RECIPE_INSTRUCTIONS -> "recipeInstructions";
      case IMAGE -> "image";
      case AUTHOR -> "author";
      case DATE_PUBLISHED -> "datePublished";
    };
  }
}
