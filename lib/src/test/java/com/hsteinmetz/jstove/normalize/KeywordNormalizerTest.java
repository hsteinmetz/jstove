package com.hsteinmetz.jstove.normalize;

import static org.junit.jupiter.api.Assertions.*;

import com.hsteinmetz.jstove.extract.FieldReader;
import org.junit.jupiter.api.Test;

/**
 * @author Hendrik Steinmetz
 */
class KeywordNormalizerTest extends GenericNormalizerTest {

  private final KeywordNormalizer normalizer = new KeywordNormalizer(new FieldReader());

  @Test
  void testNull() {
    var result = normalizer.normalize(NODE_FACTORY.nullNode(), PARSE_ISSUE_HANDLER);

    assertTrue(result.isEmpty());
  }

  @Test
  void testCommaList() {
    var list = "test1, test2, test3";
    var node = NODE_FACTORY.stringNode(list);
    var result = normalizer.normalize(node, PARSE_ISSUE_HANDLER);

    assertFalse(result.isEmpty());
    assertTrue(result.get().contains("test1"));
    assertTrue(result.get().contains("test2"));
    assertTrue(result.get().contains("test3"));

    assertFalse(result.get().contains(" test3"));
  }

  @Test
  void testSemicolonList() {
    var list = "test1; test2; test3";
    var node = NODE_FACTORY.stringNode(list);
    var result = normalizer.normalize(node, PARSE_ISSUE_HANDLER);

    assertFalse(result.isEmpty());
    assertTrue(result.get().contains("test1"));
    assertTrue(result.get().contains("test2"));
    assertTrue(result.get().contains("test3"));
  }

  @Test
  void testStringArray() {
    var node = NODE_FACTORY.arrayNode(3);
    node.add("test1");
    node.add("test2");
    node.add("test3");
    var result = normalizer.normalize(node, PARSE_ISSUE_HANDLER);

    assertFalse(result.isEmpty());
    assertTrue(result.get().contains("test1"));
    assertTrue(result.get().contains("test2"));
    assertTrue(result.get().contains("test3"));
  }

  @Test
  void testFailOnObject() {
    var node = NODE_FACTORY.objectNode();
    var result = normalizer.normalize(node, PARSE_ISSUE_HANDLER);

    assertTrue(result.isEmpty());
    assertEquals(1, PARSE_ISSUE_HANDLER.toList().size());
  }
}
