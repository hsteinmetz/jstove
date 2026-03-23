package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.api.FieldType;
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

    String title = reader.readAsText(recipeNode, FieldType.NAME.getFieldNames()).orElse(null);

    String description =
        reader.readAsText(recipeNode, FieldType.DESCRIPTION.getFieldNames()).orElse(null);

    String yield = reader.readAsText(recipeNode, FieldType.YIELD.getFieldNames()).orElse(null);

    String sourceUrl =
        reader.readAsText(recipeNode, FieldType.SOURCE_URL.getFieldNames()).orElse(null);

    List<String> keywords =
        new KeywordNormalizer(reader).normalize(recipeNode, parseIssueHandler).orElse(null);

    String cookingMethod =
        reader.readAsText(recipeNode, FieldType.COOKING_METHOD.getFieldNames()).orElse(null);

    DietType diet =
        new DietNormalizer(reader)
            .normalize(
                reader.readFirst(recipeNode, FieldType.DIET.getFieldNames()), parseIssueHandler)
            .orElse(null);

    List<String> categories =
        reader.readAsStringList(recipeNode, FieldType.CATEGORY.getFieldNames());
    List<String> cuisines = reader.readAsStringList(recipeNode, FieldType.CUISINE.getFieldNames());

    List<Ingredient> ingredients =
        new IngredientNormalizer(this.reader)
            .normalize(
                reader.readFirst(recipeNode, FieldType.RECIPE_INGREDIENT.getFieldNames()),
                parseIssueHandler)
            .orElse(List.of());
    List<InstructionSection> instructions =
        new InstructionNormalizer(this.reader)
            .normalize(
                reader.readFirst(recipeNode, FieldType.RECIPE_INSTRUCTIONS.getFieldNames()),
                parseIssueHandler)
            .orElse(List.of());

    List<MediaRef> images =
        new ImageNormalizer(this.reader)
            .normalize(
                reader.readFirst(recipeNode, FieldType.IMAGE.getFieldNames()), parseIssueHandler)
            .orElse(List.of());
    List<AuthorInfo> authors =
        new AuthorNormalizer(reader)
            .normalize(
                reader.readFirst(recipeNode, FieldType.AUTHOR.getFieldNames()), parseIssueHandler)
            .orElse(List.of());

    DurationNormalizer durationNormalizer = new DurationNormalizer(reader);
    Duration prep =
        durationNormalizer
            .normalize(
                reader.readFirst(recipeNode, FieldType.PREP_TIME.getFieldNames()),
                parseIssueHandler)
            .orElse(Duration.ZERO);
    Duration cook =
        durationNormalizer
            .normalize(
                reader.readFirst(recipeNode, FieldType.COOK_TIME.getFieldNames()),
                parseIssueHandler)
            .orElse(Duration.ZERO);
    Duration total =
        durationNormalizer
            .normalize(
                reader.readFirst(recipeNode, FieldType.TOTAL_TIME.getFieldNames()),
                parseIssueHandler)
            .orElse(Duration.ZERO);
    Duration perform =
        durationNormalizer
            .normalize(
                reader.readFirst(recipeNode, FieldType.PERFORM_TIME.getFieldNames()),
                parseIssueHandler)
            .orElse(Duration.ZERO);

    if (total.equals(Duration.ZERO)) {
      total = prep.plus(cook);
    }

    TimeInfo timeInfo = new TimeInfo(prep, cook, total, perform);

    NutritionInfo nutritionInfo =
        new NutritionNormalizer(this.reader)
            .normalize(
                reader.readFirst(recipeNode, FieldType.NUTRITION.getFieldNames()),
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
                reader.readFirst(recipeNode, FieldType.DATE_CREATED.getFieldNames()),
                parseIssueHandler)
            .orElse(null);
    ZonedDateTime dateUpdated =
        new DateNormalizer(reader)
            .normalize(
                reader.readFirst(recipeNode, FieldType.DATE_UPDATED.getFieldNames()),
                parseIssueHandler)
            .orElse(null);
    ZonedDateTime datePublished =
        new DateNormalizer(reader)
            .normalize(
                reader.readFirst(recipeNode, FieldType.DATE_PUBLISHED.getFieldNames()),
                parseIssueHandler)
            .orElse(null);
    DateInfo dateInfo = new DateInfo(dateCreated, dateUpdated, datePublished);

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
            .build();

    return Optional.of(result);
  }
}
