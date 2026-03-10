package com.hsteinmetz.jstove.extract;

import com.hsteinmetz.jstove.api.except.RecipeParseException;
import java.util.List;
import java.util.Optional;
import tools.jackson.databind.JsonNode;

/**
 * @author Hendrik Steinmetz
 */
public class FieldReader {

  public Optional<JsonNode> read(JsonNode node, String fieldName) {
    if (node == null || node.isNull() || node.isMissingNode()) {
      return Optional.empty();
    }
    JsonNode fieldNode = node.get(fieldName);
    if (fieldNode == null || fieldNode.isNull() || fieldNode.isMissingNode()) {
      return Optional.empty();
    }
    return Optional.of(fieldNode);
  }

  public List<JsonNode> readAsList(JsonNode node, String fieldName) {
    if (node == null || node.isNull() || node.isMissingNode()) {
      return List.of();
    }
    JsonNode fieldNode = node.get(fieldName);
    if (fieldNode == null || fieldNode.isNull() || fieldNode.isMissingNode()) {
      return List.of();
    }
    if (!fieldNode.isArray()) {
      throw new RecipeParseException(
          "Expected an array for field: " + fieldName + ", but got: " + fieldNode.getNodeType());
    }
    return fieldNode.findValues(fieldName);
  }

  public Optional<String> readAsText(JsonNode node, String fieldName) {
    Optional<JsonNode> fieldNodeOpt = read(node, fieldName);
    if (fieldNodeOpt.isEmpty()) {
      return Optional.empty();
    }
    JsonNode fieldNode = fieldNodeOpt.get();
    if (!fieldNode.isString()) {
      throw new RuntimeException(
          "Expected a string for field: " + fieldName + ", but got: " + fieldNode.getNodeType());
    }
    return Optional.of(fieldNode.asString());
  }

  public Optional<String> readFirstText(JsonNode node, String... fieldNames) {
    for (String fieldName : fieldNames) {
      Optional<String> valueOpt = readAsText(node, fieldName);
      if (valueOpt.isPresent()) {
        return valueOpt;
      }
    }
    return Optional.empty();
  }

  public List<String> readStringList(JsonNode node, String fieldName) {
    List<JsonNode> nodes = readAsList(node, fieldName);
    if (nodes.isEmpty()) {
      return List.of();
    }
    return nodes.stream().filter(JsonNode::isString).map(JsonNode::asString).toList();
  }
}
