package com.hsteinmetz.jstove.model;

import java.util.Map;

public record NutritionInfo(
    String calories,
    String fatContent,
    String carbohydrateContent,
    String proteinContent,
    Map<String, String> additionalInfo) {}
