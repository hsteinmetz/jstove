package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import java.util.*;
import java.util.stream.Stream;
import tools.jackson.databind.JsonNode;

/**
 * @author Hendrik Steinmetz
 */
public class KeywordNormalizer extends GenericNormalizer<List<String>> {
  /**
   * Creates a new normalizer backed by the given field reader.
   *
   * @param reader field reader used to access JSON fields
   */
  protected KeywordNormalizer(FieldReader reader) {
    super(reader);
  }

  @Override
  public Optional<List<String>> normalize(JsonNode input, ParseIssueHandler parseIssueHandler) {
    if (isBlank(input)) return Optional.empty();

    return switch (input.getNodeType()) {
      case ARRAY -> {
        var arrayNode = input.asArray();
        if (arrayNode.valueStream().allMatch(JsonNode::isString)) {
          yield Optional.of(
              arrayNode.valueStream().map(JsonNode::asString).map(String::trim).toList());
        } else {
          yield Optional.empty();
        }
      }
      case STRING -> {
        char delimiter = findDelimiter(input.asString());
        String[] parts = input.asString().split(String.valueOf(delimiter));
        yield Optional.of(Stream.of(parts).map(String::trim).toList());
      }
      case OBJECT -> {
        fail(
            parseIssueHandler,
            RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
            input.toString(),
            "Unsupported shape for keywords field; expected string or array of strings");
        yield Optional.empty();
      }
      default -> Optional.empty();
    };
  }

  private char findDelimiter(String input) {
    Map<Character, Integer> counts =
        new HashMap<>(Map.ofEntries(Map.entry(',', 0), Map.entry(';', 0), Map.entry('|', 0)));

    counts.replaceAll((c, v) -> input.chars().reduce(0, (count, ch) -> count + (ch == c ? 1 : 0)));

    return counts.entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .filter(entry -> entry.getValue() > 0)
        .map(Map.Entry::getKey)
        .orElse(',');
  }
}
