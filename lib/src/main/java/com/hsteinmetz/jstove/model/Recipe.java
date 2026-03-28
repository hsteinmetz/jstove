package com.hsteinmetz.jstove.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Hendrik Steinmetz
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Recipe {
  String title;
  String description;
  List<MediaRef> images;
  List<AuthorInfo> authors;
  List<String> keywords;

  List<Ingredient> ingredients;
  List<InstructionSection> instructionSections;

  TimeInfo time;
  String cookingMethod;
  String yield;
  NutritionInfo nutrition;

  List<String> categories;
  List<String> cuisines;

  String sourceUrl;
  SourceMetadata sourceMetadata;

  DietType suitableForDiet;

  DateInfo dateInfo;

  AggregateRating aggregateRating;

  public static Recipe empty() {
    return new Recipe();
  }
}
