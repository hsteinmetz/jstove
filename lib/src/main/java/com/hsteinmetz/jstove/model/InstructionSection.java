package com.hsteinmetz.jstove.model;

import java.util.List;
import java.util.Map;
import tools.jackson.databind.JsonNode;

public record InstructionSection(
    String name, List<InstructionStep> steps, Integer position, Map<String, JsonNode> raw)
    implements InstructionBlock {
  public static InstructionSection of(List<InstructionStep> steps, int position) {
    return new InstructionSection(null, steps, position, Map.of());
  }
}
