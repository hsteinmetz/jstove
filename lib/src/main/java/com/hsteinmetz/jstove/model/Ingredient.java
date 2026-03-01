package com.hsteinmetz.jstove.model;

import tools.jackson.databind.JsonNode;

import java.util.Map;

public record Ingredient (
        String displayText,
        String name,
        String amountText,
        String unitText,
        String note,
        Map<String, JsonNode> raw
){}
