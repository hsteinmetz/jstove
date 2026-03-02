package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.internal.WarningCollector;
import java.util.List;
import java.util.Optional;
import tools.jackson.databind.JsonNode;

/**
 * Selects the "best" node from a list of nodes. This is used to select the "best" recipe node from
 * a list of candidate nodes.
 */
public class RecipeNodeSelector {

  public Optional<JsonNode> selectBest(
      List<JsonNode> candidates, WarningCollector warningCollector) {
    if (candidates == null || candidates.isEmpty()) {
      return Optional.empty();
    }

    if (candidates.size() > 1) {
      warningCollector.warnOrThrow(
          RecipeParseErrorCode.MULTIPLE_RECIPE_NODES,
          "@root",
          "Multiple recipe nodes found; selecting best candidate",
          null);
    }

    return Optional.of(candidates.getFirst());
  }
}
