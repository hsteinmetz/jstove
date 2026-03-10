package com.hsteinmetz.jstove.normalize;

import static org.junit.jupiter.api.Assertions.*;

import com.hsteinmetz.jstove.api.ParseOptions;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import com.hsteinmetz.jstove.jackson.ObjectMapperFactory;
import java.time.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class DurationNormalizerTest {

  private static DurationNormalizer normalizer;
  private static ParseIssueHandler parseIssueHandler;
  private static ObjectMapper objectMapper;

  @BeforeAll
  static void setUp() {
    normalizer = new DurationNormalizer(new FieldReader());
    parseIssueHandler = new ParseIssueHandler(ParseOptions.defaultOptions());
    objectMapper = ObjectMapperFactory.getInstance().getObjectMapper();
  }

  @Test
  void testNormalizeNullInput() {
    assertTrue(normalizer.normalize(null, parseIssueHandler).isEmpty());
    assertTrue(
        normalizer
            .normalize(
                tools.jackson.databind.node.JsonNodeFactory.instance.nullNode(), parseIssueHandler)
            .isEmpty());
  }

  @Test
  void testNormalizeValidDuration() {
    String json = "\"PT30M\"";
    var input = objectMapper.readTree(json);
    var result = normalizer.normalize(input, parseIssueHandler);
    assertTrue(result.isPresent());
    assertEquals(Duration.ofMinutes(30), result.get());
  }

  @Test
  void testNormalizeInvalidDuration() {
    String json = "\"30 minutes\"";
    var input = objectMapper.readTree(json);
    var result = normalizer.normalize(input, parseIssueHandler);
    assertTrue(result.isEmpty());
  }
}
