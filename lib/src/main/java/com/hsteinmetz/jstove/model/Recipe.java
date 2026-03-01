package com.hsteinmetz.jstove.model;

import java.util.List;

public record Recipe(
    String title,
    String description,
    List<Ingredient> ingredients,
    List<InstructionBlock> instructions,
    TimeInfo time,
    String yield,
    NutritionInfo nutrition,
    List<String> categories,
    List<String> cuisines,
    List<MediaRef> images,
    List<AuthorInfo> authors,
    String sourceUrl,
    SourceMetadata sourceMetadata) {}
