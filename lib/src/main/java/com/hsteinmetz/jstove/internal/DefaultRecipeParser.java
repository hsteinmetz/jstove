package com.hsteinmetz.jstove.internal;

import com.hsteinmetz.jstove.api.ParseOptions;
import com.hsteinmetz.jstove.api.ParseResult;
import com.hsteinmetz.jstove.api.RecipeParser;
import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.extract.JsonReader;
import com.hsteinmetz.jstove.jackson.ObjectMapperFactory;
import com.hsteinmetz.jstove.model.Recipe;
import com.hsteinmetz.jstove.normalize.RecipeNodeLocator;
import com.hsteinmetz.jstove.normalize.RecipeNodeSelector;
import com.hsteinmetz.jstove.normalize.RecipeNormalizer;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Default implementation of the {@link RecipeParser} interface. This class is responsible for
 * parsing JSON input and producing a {@link ParseResult} that represents the parsed recipe. It
 * utilizes various components such as {@link JsonReader} for parsing JSON, {@link ParseOptions} for
 * configuring the parsing process, {@link RecipeNodeLocator} and {@link RecipeNodeSelector} for
 * locating and selecting nodes in the JSON structure, and {@link RecipeNormalizer} for normalizing
 * the parsed recipe.
 */
public final class DefaultRecipeParser implements RecipeParser {

  private final JsonReader jsonInput;
  private final ParseOptions parseOptions;
  private final RecipeNodeLocator locator;
  private final RecipeNodeSelector selector;
  private final RecipeNormalizer normalizer;

  private final Strictness strictness;

  private final ObjectMapper objectMapper = ObjectMapperFactory.getInstance().getObjectMapper();

  /**
   * Constructs a new instance of {@link DefaultRecipeParser} with the specified dependencies.
   *
   * @param jsonInput the JSON input parser
   * @param parseOptions the options for parsing
   * @param locator the recipe node locator
   * @param selector the recipe node selector
   * @param normalizer the recipe normalizer
   */
  public DefaultRecipeParser(
      JsonReader jsonInput,
      ParseOptions parseOptions,
      RecipeNodeLocator locator,
      RecipeNodeSelector selector,
      RecipeNormalizer normalizer) {
    this.jsonInput = jsonInput;
    this.parseOptions = parseOptions;
    this.locator = locator;
    this.selector = selector;
    this.normalizer = normalizer;

    this.strictness = new Strictness(this.parseOptions);
  }

  /**
   * Parses the given JSON node and produces a {@link ParseResult}.
   *
   * @param root the root JSON node to parse
   * @return a {@link ParseResult} representing the parsed recipe
   */
  @Override
  public ParseResult parse(JsonNode root) {
    WarningCollector warnings = new WarningCollector();
    List<JsonNode> candidates = locator.locate(root);
    Optional<JsonNode> best = selector.selectBest(candidates, warnings);

    if (best.isEmpty()) {
      strictness.warnOrThrow(
          warnings,
          RecipeParseErrorCode.NO_RECIPE_NODE,
          "@root",
          "No recipe node found in the input JSON",
          null);

      return new ParseResult(normalizer.emptyRecipe(), warnings.toList(), Map.of());
    }

    Optional<Recipe> normalizedResult = normalizer.normalize(best.get(), parseOptions, warnings);

    if (normalizedResult.isEmpty()) {
      strictness.warnOrThrow(
          warnings,
          RecipeParseErrorCode.RECIPE_NORMALIZATION_FAILED,
          best.get().asString(),
          "Failed to normalize the recipe node",
          null);
      return new ParseResult(normalizer.emptyRecipe(), warnings.toList(), Map.of());
    }

    Recipe normalizedRecipe = normalizedResult.get();

    return new ParseResult(normalizedRecipe, warnings.toList(), Map.of());
  }

  /**
   * Parses the given JSON string and produces a {@link ParseResult}.
   *
   * @param json the JSON string to parse
   * @return a {@link ParseResult} representing the parsed recipe
   */
  @Override
  public ParseResult parse(String json) {
    JsonNode root = jsonInput.parse(json);
    return parse(root);
  }

  /**
   * Parses the given JSON input stream and produces a {@link ParseResult}.
   *
   * @param json the JSON input stream to parse
   * @return a {@link ParseResult} representing the parsed recipe
   */
  @Override
  public ParseResult parse(InputStream json) {
    JsonNode root = jsonInput.parse(json);
    return parse(root);
  }
}
