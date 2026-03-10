package com.hsteinmetz.jstove.api;

import java.io.InputStream;
import tools.jackson.databind.JsonNode;

/**
 * Interface for parsing recipes from JSON input. This interface defines methods for parsing recipes
 * from different sources, such as a JSON string, an input stream, or a JSON node. The parsing
 * process produces a {@link ParseResult} that encapsulates the parsed recipe and any warnings or
 * errors that occurred during parsing.
 *
 * @author Hendrik Steinmetz
 */
public interface RecipeParser {

  ParseResult parse(String json);

  ParseResult parse(InputStream json);

  ParseResult parse(JsonNode root);
}
