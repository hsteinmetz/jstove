package com.hsteinmetz.jstove.api;

import tools.jackson.databind.JsonNode;

/**
 * @author Hendrik Steinmetz
 */
public interface RecipeScorer {

  int score(JsonNode node);
}
