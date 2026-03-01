package com.hsteinmetz.jstove.model;

import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

public record InstructionSection (
        String name,
        List<InstructionStep> steps,
        Integer position,
        Map<String, JsonNode> raw
) implements InstructionBlock {}
