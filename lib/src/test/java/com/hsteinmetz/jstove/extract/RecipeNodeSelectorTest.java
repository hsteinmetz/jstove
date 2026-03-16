package com.hsteinmetz.jstove.extract;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hsteinmetz.jstove.api.ParseOptions;
import com.hsteinmetz.jstove.api.RecipeScorer;
import com.hsteinmetz.jstove.api.RecipeScorers;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

/**
 * @author Hendrik Steinmetz
 */
public class RecipeNodeSelectorTest {

  private final RecipeScorer scorer = RecipeScorers.defaultScorer();
  private final RecipeNodeSelector selector = new RecipeNodeSelector(scorer);
  private final ParseIssueHandler parseIssueHandler =
      new ParseIssueHandler(ParseOptions.defaultOptions());

  @Test
  public void testSelectBest() {
    String json =
        """
          [
            {"name": "Recipe A", "ingredients": ["flour", "sugar"], "steps": ["mix", "bake"]},
            {"author": {"name": "test"}, "name": "Recipe B", "ingredients": ["flour", "sugar", "eggs"], "steps": ["mix", "bake"]},
            {"name": "Recipe C", "ingredients": ["flour"], "steps": ["mix"], "@type": "Recipe"}
          ]
          """;
    JsonReader input = new JsonReader();
    JsonNode root = input.parse(json);
    var nodesList = root.valueStream().toList();

    var scores = root.valueStream().map(scorer::score).toList();

    assertEquals(10, scores.getFirst());
    assertEquals(11, scores.get(1));
    assertEquals(20, scores.getLast());

    var best = selector.selectBest(nodesList, parseIssueHandler).orElseThrow();
    assertEquals("Recipe C", best.get("name").asString());
  }
}
