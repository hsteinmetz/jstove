package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import com.hsteinmetz.jstove.model.DietType;
import java.util.Arrays;
import java.util.Optional;
import tools.jackson.databind.JsonNode;

/**
 * @author Hendrik Steinmetz
 */
public class DietNormalizer extends GenericNormalizer<DietType> {

  protected DietNormalizer(FieldReader reader) {
    super(reader);
  }

  @Override
  public Optional<DietType> normalize(JsonNode input, ParseIssueHandler parseIssueHandler) {
    if (isBlank(input)) return Optional.empty();

    return switch (input.getNodeType()) {
      case STRING -> {
        var type = DietType.fromString(input.asString());
        if (type == null) {
          yield fail(
              parseIssueHandler,
              RecipeParseErrorCode.INVALID_DIET_TYPE,
              input.toString(),
              "Invalid value for diet field; expected one of "
                  + Arrays.stream(DietType.values()).map(DietType::getSpecName).toList());
        }

        yield Optional.of(type);
      }
      case OBJECT -> {
        throw new UnsupportedOperationException("Object shape for diet field is not supported yet");
      }
      default ->
          fail(
              parseIssueHandler,
              RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
              input.toString(),
              "Unsupported shape for diet field; expected string");
    };
  }
}
