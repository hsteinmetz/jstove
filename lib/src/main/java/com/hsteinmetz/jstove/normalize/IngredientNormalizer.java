package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import com.hsteinmetz.jstove.jackson.ObjectMapperFactory;
import com.hsteinmetz.jstove.model.Ingredient;
import com.hsteinmetz.jstove.normalize.util.NormalizationUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

// TODO: Strictness handling: Currently this fails instantly if one ingredient is malformed. While
// this may be desirable in strict mode, in lenient mode it may be better to just skip malformed
// ingredients and continue parsing the rest of the list.
// TODO: if the raw node is to be kept, store it in the ingredient object
/**
 * Normalizer for the recipeIngredient field. This field can be either an array of strings or an
 * ItemList object with an itemListElement array of strings. This normalizer handles both cases and
 * produces a list of Ingredient objects.
 *
 * <p>The normalizer performs the following steps:<br>
 * 1. Checks if the input is null, missing, or explicitly null, and returns an empty Optional if so.
 * <br>
 * 2. If the input is an array, it iterates over the elements and expects each element to be a
 * string. If an element is not a string, it logs a warning or throws an error depending on the
 * parse mode, and returns an empty Optional. <br>
 * 3. If the input is an object, it checks for the @type field and expects it to be "ItemList". If
 * the @type is missing or not "ItemList", it logs a warning or throws an error and returns an empty
 * Optional. It then checks for the itemListElement field, which should be an array. If
 * itemListElement is missing or not an array, it logs a warning or throws an error and returns an
 * empty Optional. It then iterates over the elements of itemListElement, expecting each to be a
 * string. If an element is not a string, it logs a warning or throws an error and returns an empty
 * Optional. <br>
 * 4. If the input is neither an array nor an object, it logs a warning or throws an error and
 * returns an empty Optional. <br>
 * 5. If all checks pass, it returns an Optional containing the list of Ingredient objects. This
 * normalizer ensures that the recipeIngredient field is parsed correctly according to the
 * schema.org specification, and provides robust error handling to deal with malformed input.
 *
 * @see <a href="https://schema.org/recipeIngredient">https://schema.org/recipeIngredient</a>
 * @see <a href="https://schema.org/ItemList">https://schema.org/ItemList</a>
 * @author Hendrik Steinmetz
 */
public class IngredientNormalizer extends GenericNormalizer<List<Ingredient>> {

  private final ObjectMapper mapper = ObjectMapperFactory.getInstance().getObjectMapper();

  public IngredientNormalizer(FieldReader reader) {
    super(reader);
  }

  @Override
  public Optional<List<Ingredient>> normalize(JsonNode input, ParseIssueHandler parseIssueHandler) {
    if (NormalizationUtils.isNullOrEmptyNode(input)) return Optional.empty();

    List<Ingredient> ingredients = new ArrayList<>();

    if (input.isArray()) {
      for (JsonNode node : input) {
        if (node.isString()) {
          ingredients.add(new Ingredient(node.asString(), null));
        } else {
          parseIssueHandler.warnOrThrow(
              RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
              "recipeIngredient",
              "Unsupported shape for recipeIngredient array element; expected string",
              null);
          return Optional.empty();
        }
      }
    } else if (input.isObject()) {
      var type = reader.readAsText(input, "@type").orElse(null);

      if (type == null) {
        parseIssueHandler.warnOrThrow(
            RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
            "recipeIngredient",
            "Missing @type field for recipeIngredient object",
            null);
        return Optional.empty();
      } else if (!type.equalsIgnoreCase("ItemList")) {
        parseIssueHandler.warnOrThrow(
            RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
            "recipeIngredient",
            "Unsupported @type for recipeIngredient object; expected ItemList",
            null);
        return Optional.empty();
      }

      var itemListElement = input.get("itemListElement");
      if (itemListElement == null) {
        parseIssueHandler.warnOrThrow(
            RecipeParseErrorCode.EMPTY_INGREDIENT_LIST,
            "recipeIngredient",
            "Missing itemListElement field for recipeIngredient object with @type ItemList",
            null);
        return Optional.empty();
      } else if (!itemListElement.isArray()) {
        parseIssueHandler.warnOrThrow(
            RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
            "recipeIngredient",
            "Unsupported shape for itemListElement field; expected array",
            null);
        return Optional.empty();
      }

      itemListElement = itemListElement.asArray();

      if (itemListElement.isEmpty()) {
        parseIssueHandler.warnOrThrow(
            RecipeParseErrorCode.EMPTY_INGREDIENT_LIST,
            "recipeIngredient",
            "Empty itemListElement array for recipeIngredient object with @type ItemList",
            null);
        return Optional.empty();
      }

      for (JsonNode item : itemListElement) {
        if (item.isString()) {
          ingredients.add(new Ingredient(item.asString(), null));
        } else {
          parseIssueHandler.warnOrThrow(
              RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
              "recipeIngredient",
              "Unsupported shape for itemListElement array element; expected string",
              null);

          return Optional.empty();
        }
      }
    } else {
      parseIssueHandler.warnOrThrow(
          RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
          "recipeIngredient",
          "Unsupported shape for recipeIngredient field; expected array or ItemList object",
          null);

      return Optional.empty();
    }

    return Optional.of(ingredients);
  }
}
