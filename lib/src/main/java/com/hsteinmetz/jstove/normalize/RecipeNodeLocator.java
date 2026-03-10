package com.hsteinmetz.jstove.normalize;

import java.util.ArrayList;
import java.util.List;
import tools.jackson.databind.JsonNode;

/**
 * Responsible for locating the relevant nodes in the JSON structure that represent the recipe and
 * its components. Does not perform any selection or normalization, but simply identifies the nodes
 * that should be processed by the RecipeNodeSelector and RecipeNormalizer.
 *
 * @author Hendrik Steinmetz
 */
public class RecipeNodeLocator {

  /**
   * Locates the relevant nodes in the JSON structure that represent the recipe and its components.
   * This method is a convenience method that calls the {@link RecipeNodeLocator#locate(JsonNode,
   * boolean)} method with a default depth of -1, which means to traverse the entire JSON tree.
   *
   * @param node The JSON node to start the search from. This can be the root node or any other node
   *     in the JSON.
   * @return A list of JSON nodes that represent the recipe and its components, identified by having
   *     the "@type" property set to "recipe".
   */
  public List<JsonNode> locate(JsonNode node) {
    return locate(node, true);
  }

  /**
   * Locates the relevant nodes in the JSON structure that represent the recipe and its components.
   * This method traverses the JSON tree and identifies nodes that have the "@type" property set to
   * "recipe". It returns a list of these nodes for further processing by the RecipeNodeSelector and
   * RecipeNormalizer.
   *
   * @param node The JSON node to start the search from. This can be the root node or any other node
   *     in the JSON.
   * @param recursive A boolean flag that indicates whether to traverse the JSON tree recursively.
   *     If set to false, the method will only check the provided node and not its children. If set
   *     to true, the method will traverse the entire JSON tree starting from the provided node.
   * @return A list of JSON nodes that represent the recipe and its components, identified by having
   *     the "@type" property set to "recipe".
   */
  public List<JsonNode> locate(JsonNode node, boolean recursive) {
    List<JsonNode> nodes = new ArrayList<>();

    if (node.isObject() && hasTypeRecipeNode(node)) {
      return List.of(node);
    }

    if (!recursive) {
      return List.of();
    }

    for (JsonNode child : node) {
      if (child.isObject() || child.isArray()) {
        nodes.addAll(locate(child, true));
      }
    }

    return nodes;
  }

  /**
   * Checks if the given JSON node has the "@type" property set to "recipe".
   *
   * @param node The JSON node to check.
   * @return true if the node has the "@type" property set to "recipe", false otherwise.
   */
  private boolean hasTypeRecipeNode(JsonNode node) {
    if (node.has("@type")) {
      JsonNode typeNode = node.get("@type");
      if (typeNode.isString()) {
        return node.get("@type").asString().equalsIgnoreCase("recipe");
      } else if (typeNode.isArray()) {
        for (JsonNode element : typeNode) {
          if (element.isString() && element.asString().equalsIgnoreCase("recipe")) {
            return true;
          }
        }
      }
    }

    return false;
  }
}
