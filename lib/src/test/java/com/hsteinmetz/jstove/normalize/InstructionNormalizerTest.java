package com.hsteinmetz.jstove.normalize;

import static org.junit.jupiter.api.Assertions.*;

import com.hsteinmetz.jstove.api.ParseOptions;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import com.hsteinmetz.jstove.jackson.ObjectMapperFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class InstructionNormalizerTest {

  private static ParseIssueHandler parseIssueHandler;
  private static InstructionNormalizer instructionNormalizer;
  private static ObjectMapper objectMapper;

  @BeforeAll
  static void setUp() {
    parseIssueHandler = new ParseIssueHandler(ParseOptions.defaultOptions());
    instructionNormalizer = new InstructionNormalizer(new FieldReader());
    objectMapper = ObjectMapperFactory.getInstance().getObjectMapper();
  }

  @Test
  void testNormalizeNullInput() {
    assertTrue(instructionNormalizer.normalize(null, parseIssueHandler).isEmpty());
    assertTrue(
        instructionNormalizer
            .normalize(
                tools.jackson.databind.node.JsonNodeFactory.instance.nullNode(), parseIssueHandler)
            .isEmpty());
  }

  @Test
  void testNormalizeTextInstruction() {
    String json =
        """
        {
        "recipeInstructions": "Preheat the oven to 350 degrees."
        }
        """;
    var input = objectMapper.readTree(json);
    var result = instructionNormalizer.normalize(input, parseIssueHandler);
    assertTrue(result.isPresent());
    var instructions = result.get();
    assertEquals(1, instructions.size());
    assertEquals("Preheat the oven to 350 degrees.", instructions.getFirst().text());
  }
}
