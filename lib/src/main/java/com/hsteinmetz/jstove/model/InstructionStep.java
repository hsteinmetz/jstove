package com.hsteinmetz.jstove.model;

import tools.jackson.databind.JsonNode;

import java.util.Map;

/**
 * @author Hendrik Steinmetz
 */
public record InstructionStep (
        String text,
        Integer position,
        String name,
        Map<String, JsonNode> raw
) implements InstructionBlock {}
