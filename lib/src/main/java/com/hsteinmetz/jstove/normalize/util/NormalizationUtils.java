package com.hsteinmetz.jstove.normalize.util;

import tools.jackson.databind.JsonNode;

/**
 * @author Hendrik Steinmetz
 */
public class NormalizationUtils {

  public static boolean isNullOrEmptyNode(JsonNode input) {
    return input == null || input.isNull() || input.isMissingNode();
  }
}
