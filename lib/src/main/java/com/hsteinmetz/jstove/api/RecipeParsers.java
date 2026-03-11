package com.hsteinmetz.jstove.api;

import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.extract.JsonReader;
import com.hsteinmetz.jstove.extract.RecipeNodeLocator;
import com.hsteinmetz.jstove.extract.RecipeNodeSelector;
import com.hsteinmetz.jstove.internal.DefaultRecipeParser;
import com.hsteinmetz.jstove.internal.DefaultRecipeScorer;

/**
 * Factory class for creating instances of {@link RecipeParser}. This class provides methods to
 * obtain different types of recipe parsers, such as a default parser with lenient parsing options.
 * The default parser is designed to be flexible and tolerant of various JSON structures, allowing
 * for unknown fields, missing fields, and extra fields without throwing errors. This factory class
 * serves as a convenient way to access commonly used recipe parsers without needing to configure
 * them manually.
 *
 * @author Hendrik Steinmetz
 */
public class RecipeParsers {

  private RecipeParsers() {}

  /**
   * Returns a default implementation of the {@link RecipeParser} interface with lenient parsing
   * options. This parser is configured to allow unknown fields, missing fields, and extra fields in
   * the JSON input without throwing errors. It is designed to be flexible and tolerant of various
   * JSON structures, making it suitable for a wide range of use cases where strict validation is
   * not required.
   *
   * @return a default {@link RecipeParser} instance with lenient parsing options
   */
  public static RecipeParser defaultLenientParser() {
    ParseOptions options =
        new ParseOptions(
            ParseMode.LENIENT, true // allowUnknownFields
            );

    return new DefaultRecipeParser(
        new JsonReader(),
        options,
        new RecipeNodeLocator(),
        new RecipeNodeSelector(new DefaultRecipeScorer()),
        new com.hsteinmetz.jstove.normalize.RecipeNormalizer(new FieldReader()));
  }

  public static RecipeParser defaultStrictParser() {
    ParseOptions options =
        new ParseOptions(
            ParseMode.STRICT, false // allowUnknownFields
            );

    return new DefaultRecipeParser(
        new JsonReader(),
        options,
        new RecipeNodeLocator(),
        new RecipeNodeSelector(new DefaultRecipeScorer()),
        new com.hsteinmetz.jstove.normalize.RecipeNormalizer(new FieldReader()));
  }
}
