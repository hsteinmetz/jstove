package com.hsteinmetz.jstove.api.except;

public enum RecipeParseErrorCode {
  NO_RECIPE_NODE,
  MULTIPLE_RECIPE_NODES,
  FIELD_UNSUPPORTED_SHAPE,
  FIELD_EXPECTED_TEXT,
  DURATION_INVALID,
}
