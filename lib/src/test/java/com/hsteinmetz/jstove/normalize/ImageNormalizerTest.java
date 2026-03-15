package com.hsteinmetz.jstove.normalize;

import static org.junit.jupiter.api.Assertions.*;

import com.hsteinmetz.jstove.extract.FieldReader;
import org.junit.jupiter.api.Test;

/**
 * @author Hendrik Steinmetz
 */
class ImageNormalizerTest extends GenericNormalizerTest {

  private static final ImageNormalizer normalizer = new ImageNormalizer(new FieldReader());

  @Test
  void testString() {
    String json = "\"https://example.com\"";

    var result = normalizer.normalize(MAPPER.readTree(json), PARSE_ISSUE_HANDLER);
    assertFalse(result.isEmpty());
    assertEquals(1, result.get().size());
    assertEquals("https://example.com", result.get().getFirst().url());
  }

  @Test
  void testStringArray() {
    String json = "[\"https://example.com\", \"https://test.com\"]";

    var result = normalizer.normalize(MAPPER.readTree(json), PARSE_ISSUE_HANDLER);
    assertFalse(result.isEmpty());
    assertEquals(2, result.get().size());
    assertEquals("https://example.com", result.get().getFirst().url());
    assertEquals("https://test.com", result.get().getLast().url());
  }

  @Test
  void testObjectArray() {
    String json =
        """
          [
            {"url": "https://example.com/image1.jpg", "alt": "image1"},
            {"url": "https://example.com/image2.jpg", "alt": "image2"}
          ]
          """;

    var result = normalizer.normalize(MAPPER.readTree(json), PARSE_ISSUE_HANDLER);
    assertFalse(result.isEmpty());
    assertEquals(2, result.get().size());
    assertEquals("https://example.com/image1.jpg", result.get().getFirst().url());
    assertEquals("https://example.com/image2.jpg", result.get().getLast().url());

    assertEquals("image1", result.get().getFirst().altText());
    assertEquals("image2", result.get().getLast().altText());
  }
}
