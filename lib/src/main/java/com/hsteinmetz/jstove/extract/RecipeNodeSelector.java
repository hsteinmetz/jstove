package com.hsteinmetz.jstove.extract;

import com.hsteinmetz.jstove.api.RecipeScorer;
import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import tools.jackson.databind.JsonNode;

/**
 * Selects the "best" node from a list of nodes.
 *
 * @author Hendrik Steinmetz
 */
public class RecipeNodeSelector {

  private final RecipeScorer recipeScorer;

  public RecipeNodeSelector(RecipeScorer recipeScorer) {
    this.recipeScorer = recipeScorer;
  }

  public Optional<JsonNode> selectBest(
      List<JsonNode> candidates, ParseIssueHandler parseIssueHandler) {
    if (candidates == null || candidates.isEmpty()) {
      return Optional.empty();
    }

    if (candidates.size() > 1) {
      parseIssueHandler.warnOrThrow(
          RecipeParseErrorCode.MULTIPLE_RECIPE_NODES,
          "@root",
          "Multiple recipe nodes found; selecting best candidate",
          null);

      Map<JsonNode, Integer> scoredCandidates =
          candidates.stream().collect(Collectors.toMap(node -> node, recipeScorer::score));

      return Optional.of(scoredCandidates.entrySet().stream()
          .max(Map.Entry.comparingByValue())
          .map(Map.Entry::getKey)
          .orElse(candidates.getFirst()));
    }

    return Optional.of(candidates.getFirst());
  }
}
