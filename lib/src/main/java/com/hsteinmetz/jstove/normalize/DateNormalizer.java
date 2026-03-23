package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.JsonNodeType;

/**
 * @author Hendrik Steinmetz
 */
public class DateNormalizer extends GenericNormalizer<ZonedDateTime> {

  protected DateNormalizer(FieldReader reader) {
    super(reader);
  }

  @Override
  public Optional<ZonedDateTime> normalize(JsonNode input, ParseIssueHandler parseIssueHandler) {
    if (isBlank(input) || input.getNodeType() != JsonNodeType.STRING) {
      return Optional.empty();
    }

    var content = input.asString();

    try {
      var isoResult = parseDateTimeISO(content);
      if (isoResult.isPresent()) {
        return isoResult;
      }

      var defaultResult = parseDateISO(content);
      if (defaultResult.isPresent()) {
        return defaultResult;
      }

      fail(
          parseIssueHandler,
          RecipeParseErrorCode.INVALID_DATE,
          "@root",
          "Failed to parse date: " + content + "; using null");
    } catch (Exception e) {
      fail(
          parseIssueHandler,
          RecipeParseErrorCode.INVALID_DATE,
          "@root",
          "Failed to parse date: " + content + "; using null");
    }

    return Optional.empty();
  }

  private Optional<ZonedDateTime> parseDateTimeISO(String input) {
    try {
      return Optional.of(ZonedDateTime.parse(input, DateTimeFormatter.ISO_ZONED_DATE_TIME));
    } catch (DateTimeParseException e) {
      return Optional.empty();
    }
  }

  private Optional<ZonedDateTime> parseDateISO(String input) {
    try {
      var parsed = LocalDate.parse(input, DateTimeFormatter.ISO_DATE);
      var zoned = ZonedDateTime.of(parsed, LocalTime.of(0, 0), ZoneId.of("UTC"));
      return Optional.of(zoned);
    } catch (DateTimeParseException e) {
      return Optional.empty();
    }
  }
}
