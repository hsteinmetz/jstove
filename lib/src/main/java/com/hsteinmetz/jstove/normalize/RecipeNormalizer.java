package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.api.ParseOptions;
import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.WarningCollector;
import com.hsteinmetz.jstove.model.*;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import tools.jackson.databind.JsonNode;

public class RecipeNormalizer implements GenericNormalizer<Recipe> {

  private final FieldReader reader;

  public RecipeNormalizer() {
    this.reader = new FieldReader();
  }

  public Recipe emptyRecipe() {
    return Recipe.empty();
  }

  public Optional<Recipe> normalize(
      JsonNode recipeNode, ParseOptions parseOptions, WarningCollector warningCollector) {
    if (recipeNode == null || recipeNode.isNull() || recipeNode.isMissingNode()) {
      warningCollector.addWarning(
          RecipeParseErrorCode.NO_RECIPE_NODE,
          "@root",
          "No recipe node found; using empty recipe",
          null);
      return Optional.of(emptyRecipe());
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
    List<AuthorInfo> authors =
        new AuthorNormalizer(reader)
            .normalize(
                reader.read(recipeNode, "author").orElse(null), parseOptions, warningCollector)
            .orElse(List.of());

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

    Recipe result =
        new Recipe(
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

    return Optional.of(result);
  }
}
