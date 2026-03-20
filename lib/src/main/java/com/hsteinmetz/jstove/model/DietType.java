package com.hsteinmetz.jstove.model;

/**
 * @author Hendrik Steinmetz
 */
public enum DietType {
  DIABETIC,
  GLUTEN_FREE,
  HALAL,
  HINDU,
  KOSHER,
  LOW_CALORIE,
  LOW_FAT,
  LOW_LACTOSE,
  LOW_SALT,
  VEGAN,
  VEGETARIAN;

  public String getSpecName() {
    // Convert to pascal case and append "Diet" suffix
    String baseName = this.name().toLowerCase().replace("_", " ");
    String[] words = baseName.split(" ");
    StringBuilder specName = new StringBuilder();
    for (String word : words) {
      specName.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
    }
    return specName.append("Diet").toString();
  }

  public static DietType fromString(String input) {
    for (DietType dietType : DietType.values()) {
      if (dietType.getSpecName().equalsIgnoreCase(input)) {
        return dietType;
      }
    }

    return null;
  }
}
