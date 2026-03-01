package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.api.ParseOptions;
import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.WarningCollector;
import com.hsteinmetz.jstove.model.*;
import java.time.Duration;
import java.util.List;
import tools.jackson.databind.JsonNode;

public class RecipeNormalizer {

  private final FieldReader reader;

  public RecipeNormalizer() {
    this.reader = new FieldReader();
  }

  public Recipe emptyRecipe() {
    return new Recipe(null, null, null, null, null, null, null, null, null, null, null, null, null);
  }

  public Recipe normalize(
      JsonNode recipeNode, ParseOptions parseOptions, WarningCollector warningCollector) {
    if (recipeNode == null || recipeNode.isNull() || recipeNode.isMissingNode()) {
      warningCollector.addWarning(
          RecipeParseErrorCode.NO_RECIPE_NODE,
          "@root",
          "No recipe node found; using empty recipe",
          null);
      return emptyRecipe();
    }
    String title = reader.readFirstText(recipeNode, "name", "headline", "title").orElse(null);
    String description = reader.readAsText(recipeNode, "description").orElse(null);
    String yield = reader.readFirstText(recipeNode, "yield", "recipeYield").orElse(null);
    String sourceUrl = reader.readFirstText(recipeNode, "url", "mainEntityOfPage").orElse(null);

    List<String> categories = reader.readStringList(recipeNode, "recipeCategory");
    List<String> cuisines = reader.readStringList(recipeNode, "recipeCuisine");

    // TODO
    List<Ingredient> ingredients = List.of();
    List<InstructionBlock> instructions = List.of();
    List<MediaRef> images = List.of();
    List<AuthorInfo> authors = List.of();

    Duration prep = Duration.ZERO;
    Duration cook = Duration.ZERO;
    Duration total = Duration.ZERO;
    TimeInfo timeInfo = new TimeInfo(prep, cook, total);

    NutritionInfo nutritionInfo = new NutritionInfo(null, null, null, null, null);

    SourceMetadata sourceMetadata =
        new SourceMetadata(
            "json-ld",
            reader.readAsText(recipeNode, "@type").orElse(""),
            parseOptions.keepSourceNode() ? recipeNode : null);

    return new Recipe(
        title,
        description,
        ingredients,
        instructions,
        timeInfo,
        yield,
        nutritionInfo,
        categories,
        cuisines,
        images,
        authors,
        sourceUrl,
        sourceMetadata);
  }
}
