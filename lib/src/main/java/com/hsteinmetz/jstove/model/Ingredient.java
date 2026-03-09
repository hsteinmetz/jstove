package com.hsteinmetz.jstove.model;

import java.util.Map;
import tools.jackson.databind.JsonNode;

public record Ingredient (
        String displayText,
        String name,
        String amountText,
        String unitText,
        String note,
        Map<String, JsonNode> raw
){
    public Ingredient(String displayText) {
        this(displayText, null, null, null, null, Map.of());
    }
}
