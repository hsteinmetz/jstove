package com.hsteinmetz.jstove.normalize;

import static org.junit.jupiter.api.Assertions.*;

import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.extract.FieldReader;
import org.junit.jupiter.api.Test;

/**
 * @author Hendrik Steinmetz
 */
class DateNormalizerTest extends GenericNormalizerTest {

  private final DateNormalizer normalizer = new DateNormalizer(new FieldReader());

  @Test
  void testNull() {
    var result = normalizer.normalize(NODE_FACTORY.nullNode(), PARSE_ISSUE_HANDLER);

    assertTrue(result.isEmpty());
    assertTrue(PARSE_ISSUE_HANDLER.toList().isEmpty());
  }

  @Test
  void testDateOnlyIsoString() {
    var pureDateString = "1970-01-01";
    var result = normalizer.normalize(NODE_FACTORY.stringNode(pureDateString), PARSE_ISSUE_HANDLER);

    assertFalse(result.isEmpty());
    assertEquals(1970, result.get().getYear());
  }

  @Test
  void testZonedIsoDateTimeString() {
    var pureDateString = "1970-01-01T01:05:00Z";
    var result = normalizer.normalize(NODE_FACTORY.stringNode(pureDateString), PARSE_ISSUE_HANDLER);

    assertFalse(result.isEmpty());
    assertEquals(1970, result.get().getYear());
    assertEquals(5, result.get().getMinute());
  }

  @Test
  void testEmptyForNonStringNode() {
    var result = normalizer.normalize(NODE_FACTORY.numberNode(42), PARSE_ISSUE_HANDLER);

    assertTrue(result.isEmpty());
    assertTrue(PARSE_ISSUE_HANDLER.toList().isEmpty());
  }

  @Test
  void testEmptyAndReportsInvalidDateForMalformedStrings() {
    var invalidMonth = "1970-13-13T00:00:00Z";
    var result = normalizer.normalize(NODE_FACTORY.stringNode(invalidMonth), PARSE_ISSUE_HANDLER);
    assertTrue(result.isEmpty());

    var invalidYear = "abc-13-13T00:00:00Z";
    result = normalizer.normalize(NODE_FACTORY.stringNode(invalidYear), PARSE_ISSUE_HANDLER);
    assertTrue(result.isEmpty());

    var invalidDay = "1970-13-95:00:00Z";
    result = normalizer.normalize(NODE_FACTORY.stringNode(invalidDay), PARSE_ISSUE_HANDLER);
    assertTrue(result.isEmpty());

    var invalidDateIssueCount =
        PARSE_ISSUE_HANDLER.toList().stream()
            .filter(issue -> issue.code() == RecipeParseErrorCode.INVALID_DATE)
            .count();
    assertEquals(3, invalidDateIssueCount);
  }

  @Test
  void testEmptyAndReportsInvalidDateForEmptyString() {
    var result = normalizer.normalize(NODE_FACTORY.stringNode(""), PARSE_ISSUE_HANDLER);

    assertTrue(result.isEmpty());

    var invalidDateIssueCount =
        PARSE_ISSUE_HANDLER.toList().stream()
            .filter(issue -> issue.code() == RecipeParseErrorCode.INVALID_DATE)
            .count();
    assertEquals(1, invalidDateIssueCount);
  }
}
