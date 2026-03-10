package com.hsteinmetz.jstove.model;

import java.util.Map;
import tools.jackson.databind.JsonNode;

/**
 * @author Hendrik Steinmetz
 */
public record InstructionStep(
    String text, Integer position, String name, Map<String, JsonNode> raw) {}
