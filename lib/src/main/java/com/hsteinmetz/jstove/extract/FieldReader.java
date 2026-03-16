package com.hsteinmetz.jstove.extract;

import com.hsteinmetz.jstove.normalize.util.NormalizationUtils;
import java.util.List;
import java.util.Optional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.JsonNodeType;

/**
 * @author Hendrik Steinmetz
 */
public class FieldReader {

  private static final String SCHEMA_ORG_PREFIX = "http://schema.org/";
  private boolean useSchemaOrgPrefix = true;

  public FieldReader(boolean useSchemaOrgPrefix) {
    this.useSchemaOrgPrefix = useSchemaOrgPrefix;
  }

  public FieldReader() {}

  public Optional<JsonNode> read(JsonNode node, String fieldName) {
    if (NormalizationUtils.isNullOrEmptyNode(node)) {
      return Optional.empty();
    }

    return this.get(node, fieldName);
  }

  public Optional<JsonNode> readFirst(JsonNode node, List<String> fieldNames) {
    for (String fieldName : fieldNames) {
      Optional<JsonNode> valueOpt = read(node, fieldName);
      if (valueOpt.isPresent()) {
        return valueOpt;
      }
    }
    return Optional.empty();
  }

  public List<JsonNode> readAsList(JsonNode node, List<String> fieldNames) {
    var firstNode = readFirst(node, fieldNames);

    return firstNode.map(jsonNode -> jsonNode.asArray().valueStream().toList()).orElseGet(List::of);
  }

  public List<JsonNode> readAsList(JsonNode node, String fieldName) {
    return readAsList(node, List.of(fieldName));
  }

  public List<String> readAsStringList(JsonNode node, String fieldName) {
    return readAsList(node, fieldName).stream().map(JsonNode::asString).toList();
  }

  public List<String> readAsStringList(JsonNode node, List<String> fieldNames) {
    return readAsList(node, fieldNames).stream().map(JsonNode::asString).toList();
  }

  public Optional<String> readAsText(JsonNode node, List<String> fieldNames) {
    var firstNode = readFirst(node, fieldNames);

    return firstNode.map(JsonNode::asString);
  }

  public Optional<String> readAsText(JsonNode node, String fieldName) {
    return readAsText(node, List.of(fieldName));
  }

  public boolean has(JsonNode node, String fieldName) {
    return this.get(node, fieldName).isPresent();
  }

  public boolean isOfType(JsonNode node, String fieldName, JsonNodeType expectedType) {
    var field = this.get(node, fieldName);

    return field.map(jsonNode -> jsonNode.getNodeType().equals(expectedType)).orElse(false);
  }

  private Optional<JsonNode> get(JsonNode node, String fieldName) {
    if (NormalizationUtils.isNullOrEmptyNode(node)) {
      return Optional.empty();
    }

    if (node.has(fieldName)) {
      return Optional.of(node.get(fieldName));
    }

    if (this.useSchemaOrgPrefix && node.has(SCHEMA_ORG_PREFIX + fieldName)) {
      return Optional.of(node.get(SCHEMA_ORG_PREFIX + fieldName));
    }

    return Optional.empty();
  }
}
