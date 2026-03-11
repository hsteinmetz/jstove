package com.hsteinmetz.jstove.normalize.util;

import tools.jackson.databind.JsonNode;

/**
 * @author Hendrik Steinmetz
 */
public enum NodeShape {
  STRING,
  NUMBER,
  BOOLEAN,
  NULL,
  OBJECT,
  ARRAY,
  UNKNOWN;

  public static NodeShape of(JsonNode node) {
    if (node.isString()) {
      return STRING;
    } else if (node.isNumber()) {
      return NUMBER;
    } else if (node.isBoolean()) {
      return BOOLEAN;
    } else if (node.isNull()) {
      return NULL;
    } else if (node.isObject()) {
      return OBJECT;
    } else if (node.isArray()) {
      return ARRAY;
    } else {
      return UNKNOWN;
    }
  }
}
