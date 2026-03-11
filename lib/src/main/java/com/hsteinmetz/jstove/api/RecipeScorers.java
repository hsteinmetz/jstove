package com.hsteinmetz.jstove.api;

/**
 * @author Hendrik Steinmetz
 */
public class RecipeScorers {

  private RecipeScorers() {}

  public static RecipeScorer defaultScorer() {
    return new com.hsteinmetz.jstove.internal.DefaultRecipeScorer();
  }
}
