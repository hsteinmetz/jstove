package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import com.hsteinmetz.jstove.model.NutritionInfo;
import com.hsteinmetz.jstove.normalize.util.NormalizationUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import tools.jackson.databind.JsonNode;

/**
 * @author Hendrik Steinmetz
 */
public class NutritionNormalizer extends GenericNormalizer<NutritionInfo> {

  public NutritionNormalizer(FieldReader reader) {
    super(reader);
  }

  @Override
  public Optional<NutritionInfo> normalize(JsonNode input, ParseIssueHandler parseIssueHandler) {
    if (NormalizationUtils.isNullOrEmptyNode(input)) return Optional.empty();

    var calories = reader.readAsText(input, "calories").orElse(null);
    var fatContent = reader.readAsText(input, "fatContent").orElse(null);
    var carbohydrateContent = reader.readAsText(input, "carbohydrateContent").orElse(null);
    var proteinContent = reader.readAsText(input, "proteinContent").orElse(null);

    var filter =
        List.of("calories", "fatContent", "carbohydrateContent", "proteinContent", "@type");

    var additionalInfo =
        input.properties().stream()
            .filter(prop -> !filter.contains(prop.getKey()))
            .collect(
                java.util.stream.Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> reader.readAsText(input, entry.getKey()).orElse("")));

    return Optional.of(
        new NutritionInfo(
            calories, fatContent, carbohydrateContent, proteinContent, additionalInfo));
  }
}
