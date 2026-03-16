package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.api.FieldNameProvider;
import com.hsteinmetz.jstove.api.FieldNameProviders;
import com.hsteinmetz.jstove.api.FieldType;
import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import com.hsteinmetz.jstove.model.*;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import tools.jackson.databind.JsonNode;

/**
 * @author Hendrik Steinmetz
 */
public class RecipeNormalizer extends GenericNormalizer<Recipe> {

  // TODO revise this if fieldnameprovider should be customizable
  private final FieldNameProvider fieldNameProvider = FieldNameProviders.defaultFieldNameProvider();

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

    String title =
        reader
            .readAsText(recipeNode, fieldNameProvider.getFieldNamesForType(FieldType.NAME))
            .orElse(null);

    String description =
        reader
            .readAsText(recipeNode, fieldNameProvider.getFieldNamesForType(FieldType.DESCRIPTION))
            .orElse(null);

    String yield =
        reader
            .readAsText(recipeNode, fieldNameProvider.getFieldNamesForType(FieldType.YIELD))
            .orElse(null);

    String sourceUrl =
        reader
            .readAsText(recipeNode, fieldNameProvider.getFieldNamesForType(FieldType.SOURCE_URL))
            .orElse(null);

    // TODO: keywords normalizer
    List<String> keywords =
        reader.readAsStringList(
            recipeNode, fieldNameProvider.getFieldNamesForType(FieldType.KEYWORDS));

    String cookingMethod =
        reader
            .readAsText(
                recipeNode, fieldNameProvider.getFieldNamesForType(FieldType.COOKING_METHOD))
            .orElse(null);

    // TODO: Diet normalizer
    DietType diet = null;

    List<String> categories =
        reader.readAsStringList(
            recipeNode, fieldNameProvider.getFieldNamesForType(FieldType.CATEGORY));
    List<String> cuisines =
        reader.readAsStringList(
            recipeNode, fieldNameProvider.getFieldNamesForType(FieldType.CUISINE));

    List<Ingredient> ingredients =
        new IngredientNormalizer(this.reader)
            .normalize(
                reader.readFirst(
                    recipeNode,
                    fieldNameProvider.getFieldNamesForType(FieldType.RECIPE_INGREDIENT)),
                parseIssueHandler)
            .orElse(List.of());
    List<InstructionSection> instructions =
        new InstructionNormalizer(this.reader)
            .normalize(
                reader.readFirst(
                    recipeNode,
                    fieldNameProvider.getFieldNamesForType(FieldType.RECIPE_INSTRUCTIONS)),
                parseIssueHandler)
            .orElse(List.of());

    List<MediaRef> images =
        new ImageNormalizer(this.reader)
            .normalize(
                reader.readFirst(
                    recipeNode, fieldNameProvider.getFieldNamesForType(FieldType.IMAGE)),
                parseIssueHandler)
            .orElse(List.of());
    List<AuthorInfo> authors =
        new AuthorNormalizer(reader)
            .normalize(
                reader.readFirst(
                    recipeNode, fieldNameProvider.getFieldNamesForType(FieldType.AUTHOR)),
                parseIssueHandler)
            .orElse(List.of());

    DurationNormalizer durationNormalizer = new DurationNormalizer(reader);
    Duration prep =
        durationNormalizer
            .normalize(
                reader.readFirst(
                    recipeNode, fieldNameProvider.getFieldNamesForType(FieldType.PREP_TIME)),
                parseIssueHandler)
            .orElse(Duration.ZERO);
    Duration cook =
        durationNormalizer
            .normalize(
                reader.readFirst(
                    recipeNode, fieldNameProvider.getFieldNamesForType(FieldType.COOK_TIME)),
                parseIssueHandler)
            .orElse(Duration.ZERO);
    Duration total =
        durationNormalizer
            .normalize(
                reader.readFirst(
                    recipeNode, fieldNameProvider.getFieldNamesForType(FieldType.TOTAL_TIME)),
                parseIssueHandler)
            .orElse(Duration.ZERO);
    Duration perform =
        durationNormalizer
            .normalize(
                reader.readFirst(
                    recipeNode, fieldNameProvider.getFieldNamesForType(FieldType.PERFORM_TIME)),
                parseIssueHandler)
            .orElse(Duration.ZERO);

    if (total.equals(Duration.ZERO)) {
      total = prep.plus(cook);
    }

    TimeInfo timeInfo = new TimeInfo(prep, cook, total, perform);

    NutritionInfo nutritionInfo =
        new NutritionNormalizer(this.reader)
            .normalize(
                reader.readFirst(
                    recipeNode, fieldNameProvider.getFieldNamesForType(FieldType.NUTRITION)),
                parseIssueHandler)
            .orElse(null);

    SourceMetadata sourceMetadata =
        new SourceMetadata(
            "json-ld",
            reader.readAsText(recipeNode, "@type").orElse(""),
            parseIssueHandler.getParseOptions().keepSourceNode() ? recipeNode : null);

    // TODO
    Date dateCreated = new Date();
    Date dateUpdated = new Date();
    Date datePublished = new Date();
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
