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
    SourceMetadata sourceMetadata) {
  public static Recipe empty() {
    return new Recipe(
        null,
        null,
        List.of(),
        List.of(),
        new TimeInfo(null, null, null),
        null,
        new NutritionInfo(null, null, null, null, null),
        List.of(),
        List.of(),
        List.of(),
        List.of(),
        null,
        null);
  }
}
