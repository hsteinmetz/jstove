package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.api.RecipeField;
import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import com.hsteinmetz.jstove.model.*;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import tools.jackson.databind.JsonNode;

/**
 * @author Hendrik Steinmetz
 */
public class RecipeNormalizer extends GenericNormalizer<Recipe> {

  public RecipeNormalizer(FieldReader reader) {
    super(reader);
  }

  public Recipe emptyRecipe() {
    return Recipe.empty();
  }

  /**
   * Normalizes a JSON node containing a recipe into a {@link Recipe} object.
   *
   * @param recipeNode the JSON node to normalize
   * @param parseIssueHandler the handler for parsing issues that may arise during normalization
   * @return an Optional containing the normalized Recipe, or an empty Optional if normalization
   *     fails
   */
  public Optional<Recipe> normalize(JsonNode recipeNode, ParseIssueHandler parseIssueHandler) {
    if (isBlank(recipeNode)) {
      return fail(
          parseIssueHandler,
          RecipeParseErrorCode.NO_RECIPE_NODE,
          "@root",
          "No recipe node found; using empty recipe");
    }

    String title = reader.readAsText(recipeNode, RecipeField.NAME.getFieldNames()).orElse(null);

    String description =
        reader.readAsText(recipeNode, RecipeField.DESCRIPTION.getFieldNames()).orElse(null);

    String yield = reader.readAsText(recipeNode, RecipeField.YIELD.getFieldNames()).orElse(null);

    String sourceUrl =
        reader.readAsText(recipeNode, RecipeField.SOURCE_URL.getFieldNames()).orElse(null);

    List<String> keywords =
        new KeywordNormalizer(reader).normalize(recipeNode, parseIssueHandler).orElse(null);

    String cookingMethod =
        reader.readAsText(recipeNode, RecipeField.COOKING_METHOD.getFieldNames()).orElse(null);

    DietType diet =
        new DietNormalizer(reader)
            .normalize(
                reader.readFirst(recipeNode, RecipeField.DIET.getFieldNames()), parseIssueHandler)
            .orElse(null);

    List<String> categories =
        reader.readAsStringList(recipeNode, RecipeField.CATEGORY.getFieldNames());
    List<String> cuisines =
        reader.readAsStringList(recipeNode, RecipeField.CUISINE.getFieldNames());

    List<Ingredient> ingredients =
        new IngredientNormalizer(this.reader)
            .normalize(
                reader.readFirst(recipeNode, RecipeField.RECIPE_INGREDIENT.getFieldNames()),
                parseIssueHandler)
            .orElse(List.of());
    List<InstructionSection> instructions =
        new InstructionNormalizer(this.reader)
            .normalize(
                reader.readFirst(recipeNode, RecipeField.RECIPE_INSTRUCTIONS.getFieldNames()),
                parseIssueHandler)
            .orElse(List.of());

    List<MediaRef> images =
        new ImageNormalizer(this.reader)
            .normalize(
                reader.readFirst(recipeNode, RecipeField.IMAGE.getFieldNames()), parseIssueHandler)
            .orElse(List.of());
    List<AuthorInfo> authors =
        new AuthorNormalizer(reader)
            .normalize(
                reader.readFirst(recipeNode, RecipeField.AUTHOR.getFieldNames()), parseIssueHandler)
            .orElse(List.of());

    DurationNormalizer durationNormalizer = new DurationNormalizer(reader);
    Duration prep =
        durationNormalizer
            .normalize(
                reader.readFirst(recipeNode, RecipeField.PREP_TIME.getFieldNames()),
                parseIssueHandler)
            .orElse(Duration.ZERO);
    Duration cook =
        durationNormalizer
            .normalize(
                reader.readFirst(recipeNode, RecipeField.COOK_TIME.getFieldNames()),
                parseIssueHandler)
            .orElse(Duration.ZERO);
    Duration total =
        durationNormalizer
            .normalize(
                reader.readFirst(recipeNode, RecipeField.TOTAL_TIME.getFieldNames()),
                parseIssueHandler)
            .orElse(Duration.ZERO);
    Duration perform =
        durationNormalizer
            .normalize(
                reader.readFirst(recipeNode, RecipeField.PERFORM_TIME.getFieldNames()),
                parseIssueHandler)
            .orElse(Duration.ZERO);

    if (total.equals(Duration.ZERO)) {
      total = prep.plus(cook);
    }

    TimeInfo timeInfo = new TimeInfo(prep, cook, total, perform);

    NutritionInfo nutritionInfo =
        new NutritionNormalizer(this.reader)
            .normalize(
                reader.readFirst(recipeNode, RecipeField.NUTRITION.getFieldNames()),
                parseIssueHandler)
            .orElse(null);

    SourceMetadata sourceMetadata =
        new SourceMetadata(
            "json-ld",
            reader.readAsText(recipeNode, "@type").orElse(""),
            parseIssueHandler.getParseOptions().keepSourceNode() ? recipeNode : null);

    ZonedDateTime dateCreated =
        new DateNormalizer(reader)
            .normalize(
                reader.readFirst(recipeNode, RecipeField.DATE_CREATED.getFieldNames()),
                parseIssueHandler)
            .orElse(null);
    ZonedDateTime dateUpdated =
        new DateNormalizer(reader)
            .normalize(
                reader.readFirst(recipeNode, RecipeField.DATE_UPDATED.getFieldNames()),
                parseIssueHandler)
            .orElse(null);
    ZonedDateTime datePublished =
        new DateNormalizer(reader)
            .normalize(
                reader.readFirst(recipeNode, RecipeField.DATE_PUBLISHED.getFieldNames()),
                parseIssueHandler)
            .orElse(null);
    DateInfo dateInfo = new DateInfo(dateCreated, dateUpdated, datePublished);

    AggregateRating aggregateRating =
        new AggregateRatingNormalizer(reader)
            .normalize(
                reader.readFirst(recipeNode, RecipeField.AGGREGATE_RATING.getFieldNames()),
                parseIssueHandler)
            .orElse(null);

    CostInfo costInfo =
        new EstimatedCostNormalizer(reader)
            .normalize(
                reader.readFirst(recipeNode, RecipeField.ESTIMATED_COST.getFieldNames()),
                parseIssueHandler)
            .orElse(null);

    Recipe result =
        Recipe.builder()
            .title(title)
            .description(description)
            .yield(yield)
            .sourceUrl(sourceUrl)
            .categories(categories)
            .keywords(keywords)
            .cookingMethod(cookingMethod)
            .suitableForDiet(diet)
            .cuisines(cuisines)
            .ingredients(ingredients)
            .instructionSections(instructions)
            .time(timeInfo)
            .nutrition(nutritionInfo)
            .images(images)
            .authors(authors)
            .sourceMetadata(sourceMetadata)
            .dateInfo(dateInfo)
            .aggregateRating(aggregateRating)
            .costInfo(costInfo)
            .build();

    return Optional.of(result);
  }
}
