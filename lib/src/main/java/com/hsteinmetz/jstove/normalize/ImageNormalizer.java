package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import com.hsteinmetz.jstove.model.MediaRef;
import com.hsteinmetz.jstove.normalize.util.NodeShape;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import tools.jackson.databind.JsonNode;

/**
 * @author Hendrik Steinmetz
 */
public class ImageNormalizer extends GenericNormalizer<List<MediaRef>> {
  protected ImageNormalizer(FieldReader reader) {
    super(reader);
  }

  /**
   * Normalizes the image field from the given JSON node. The method handles various input formats
   * such as arrays, objects and pure strings
   *
   * @param input The input JSON node
   * @param parseIssueHandler A {@link ParseIssueHandler} to handle errors
   * @return An {@link Optional} containing the list with the image data
   */
  @Override
  public Optional<List<MediaRef>> normalize(JsonNode input, ParseIssueHandler parseIssueHandler) {
    if (isBlank(input)) return Optional.empty();

    return switch (NodeShape.of(input)) {
      case STRING -> {
        var url = input.asString();
        yield Optional.of(List.of(MediaRef.of(url, null)));
      }
      case ARRAY -> Optional.of(readArray(input, parseIssueHandler));
      case OBJECT -> {
        var result = new ArrayList<MediaRef>();
        var parsed = readSingleObject(input, parseIssueHandler);
        parsed.ifPresent(result::add);

        yield Optional.of(result);
      }
      default ->
          fail(
              parseIssueHandler,
              RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
              input.toString(),
              "Unsupported shape for image field; expected string, array of strings, or object with 'url' field");
    };
  }

  private List<MediaRef> readArray(JsonNode input, ParseIssueHandler parseIssueHandler) {
    var arrayNode = input.asArray();
    var types = arrayNode.elements().stream().map(NodeShape::of).toList();

    if (types.stream().allMatch(shape -> shape == NodeShape.STRING)) {
      return arrayNode.elements().stream()
          .map(JsonNode::asString)
          .map(url -> MediaRef.of(url, null))
          .toList();
    } else if (types.stream().allMatch(shape -> shape == NodeShape.OBJECT)) {
      return arrayNode.elements().stream()
          .map(node -> readSingleObject(node, parseIssueHandler).orElse(null))
          .filter(Objects::nonNull)
          .toList();
    }

    return List.of();
  }

  private Optional<MediaRef> readSingleObject(JsonNode input, ParseIssueHandler parseIssueHandler) {
    var url = reader.readAsText(input, "url").orElse(null);
    var alt = reader.readAsText(input, "alt").orElse(null);
    if (url == null) {
      parseIssueHandler.warnOrThrow(
          RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
          "image",
          "Image object in array is missing required 'url' field",
          null);
      return Optional.empty();
    }

    return Optional.of(MediaRef.of(url, alt));
  }
}
