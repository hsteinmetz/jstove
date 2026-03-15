package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.model.AuthorInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.JsonNodeFactory;

/**
 * @author Hendrik Steinmetz
 */
class AuthorNormalizerTest extends GenericNormalizerTest {

  private static final AuthorNormalizer normalizer = new AuthorNormalizer(new FieldReader());

  @Test
  void testAuthorNormalizeReturnsEmptyOnNull() {
    Assertions.assertTrue(normalizer.normalize(null, PARSE_ISSUE_HANDLER).isEmpty());
  }

  @Test
  void testAuthorNormalizerTextOnly() {
    JsonNode node = JsonNodeFactory.instance.objectNode().put("author", "Jane Doe");
    var result = normalizer.normalize(node.get("author"), PARSE_ISSUE_HANDLER);
    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals("Jane Doe", result.get().getFirst().name());
  }

  @Test
  void testAuthorNormalizerArrayOfStrings() {
    String json =
        """
      {
        "author": ["Jane Doe", "John Smith"]
      }
    """;
    JsonNode node = MAPPER.readTree(json);
    var result = normalizer.normalize(node.get("author"), PARSE_ISSUE_HANDLER);
    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals(2, result.get().size());
    Assertions.assertEquals("Jane Doe", result.get().get(0).name());
    Assertions.assertEquals("John Smith", result.get().get(1).name());
  }

  @Test
  void testAuthorObject() {
    String json =
        """
      {
        "author": {
          "name": "Jane Doe",
          "email": "janedoe@acme.com",
          "url": "https://example.com/janedoe"
        }
      }
    """;
    JsonNode node = MAPPER.readTree(json);
    var result = normalizer.normalize(node.get("author"), PARSE_ISSUE_HANDLER);
    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals(1, result.get().size());
    AuthorInfo author = result.get().getFirst();
    Assertions.assertEquals("Jane Doe", author.name());
    Assertions.assertEquals("janedoe@acme.com", author.email());
    Assertions.assertEquals("https://example.com/janedoe", author.url());
  }

  @Test
  void testAuthorArrayOfObjects() {
    String json =
        """
      {
        "author": [
          {
            "name": "Jane Doe",
            "email": "janedoe@acme.com",
            "url": "https://example.com/janedoe"
          },
          {
            "name": "John Smith",
            "email": "johnsmith@gmail.com",
            "url": "https://example.com/johnsmith"
          }
        ]
      }
    """;
    JsonNode node = MAPPER.readTree(json);
    var result = normalizer.normalize(node.get("author"), PARSE_ISSUE_HANDLER);
    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals(2, result.get().size());
    AuthorInfo author1 = result.get().get(0);
    AuthorInfo author2 = result.get().get(1);
    Assertions.assertEquals("Jane Doe", author1.name());
    Assertions.assertEquals("janedoe@acme.com", author1.email());
    Assertions.assertEquals("https://example.com/janedoe", author1.url());

    Assertions.assertEquals("John Smith", author2.name());
    Assertions.assertEquals("johnsmith@gmail.com", author2.email());
    Assertions.assertEquals("https://example.com/johnsmith", author2.url());
  }

  @Test
  void testAuthorNormalizerUnsupportedShape() {
    String json =
        """
        {
            "author": 12345
            }
        """;
    JsonNode node = MAPPER.readTree(json);
    var result = normalizer.normalize(node.get("author"), PARSE_ISSUE_HANDLER);
    Assertions.assertTrue(result.isEmpty());
    Assertions.assertFalse(PARSE_ISSUE_HANDLER.isEmpty());
    Assertions.assertEquals(1, PARSE_ISSUE_HANDLER.toList().size());
  }
}
