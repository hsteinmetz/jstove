package com.hsteinmetz.jstove.normalize;

import static org.junit.jupiter.api.Assertions.*;

import com.hsteinmetz.jstove.api.ParseOptions;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import com.hsteinmetz.jstove.jackson.ObjectMapperFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

/**
 * @author Hendrik Steinmetz
 */
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

  @BeforeEach
  void clearParseIssues() {
    parseIssueHandler.clear();
  }

  @Test
  void testNormalizeNullInput() {
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
        "Preheat the oven to 350 degrees."
        """;
    var input = objectMapper.readTree(json);
    var result = instructionNormalizer.normalize(input, parseIssueHandler);
    assertTrue(result.isPresent());
    var sections = result.get();
    assertEquals(1, sections.size());
    var instructions = sections.getFirst().steps();
    assertEquals(1, instructions.size());
    assertEquals("Preheat the oven to 350 degrees.", instructions.getFirst().text());
  }

  @Test
  void testNormalizeArrayOfTextInstructions() {
    String json =
        """
          [
            "Preheat the oven to 350 degrees.",
            "Mix the flour and sugar.",
            "Bake for 30 minutes."
          ]
          """;
    var input = objectMapper.readTree(json);
    var result = instructionNormalizer.normalize(input, parseIssueHandler);
    assertTrue(result.isPresent());
    var sections = result.get();
    assertEquals(1, sections.size());
    var instructions = sections.getFirst().steps();
    assertEquals(3, instructions.size());
    assertEquals("Preheat the oven to 350 degrees.", instructions.get(0).text());
    assertEquals("Mix the flour and sugar.", instructions.get(1).text());
    assertEquals("Bake for 30 minutes.", instructions.get(2).text());

    assertEquals(0, instructions.get(0).position());
    assertEquals(1, instructions.get(1).position());
    assertEquals(2, instructions.get(2).position());
  }

  @Test
  void testNormalizeHowToStepsWithoutSections() {
    String json =
        """
          [
            {
              "@type": "HowToStep",
              "text": "Preheat the oven to 350 degrees."
            },
            {
              "@type": "HowToStep",
              "text": "Mix the flour and sugar."
            },
            {
              "@type": "HowToStep",
              "text": "Bake for 30 minutes."
            }
          ]
          """;
    var input = objectMapper.readTree(json);
    var result = instructionNormalizer.normalize(input, parseIssueHandler);
    assertTrue(result.isPresent());
    var section = result.get();
    assertEquals(1, section.size());
    var instructions = section.getFirst().steps();
    assertEquals(3, instructions.size());
    assertEquals("Preheat the oven to 350 degrees.", instructions.get(0).text());
    assertEquals("Mix the flour and sugar.", instructions.get(1).text());
    assertEquals("Bake for 30 minutes.", instructions.get(2).text());

    assertEquals(0, instructions.get(0).position());
    assertEquals(1, instructions.get(1).position());
    assertEquals(2, instructions.get(2).position());
  }

  @Test
  void testNormalizeHowToStepsWithSections() {
    String json =
        """
            [
              {
                "@type": "HowToSection",
                "name": "Preparation",
                "itemListElement": [
                  {
                    "@type": "HowToStep",
                    "text": "Preheat the oven to 350 degrees."
                  },
                  {
                    "@type": "HowToStep",
                    "text": "Mix the flour and sugar."
                  }
                ]
              },
              {
                "@type": "HowToSection",
                "name": "Cooking",
                "itemListElement": [
                  {
                    "@type": "HowToStep",
                    "text": "Bake for 30 minutes."
                  }
                ]
              }
            ]
            """;
    var input = objectMapper.readTree(json);
    var result = instructionNormalizer.normalize(input, parseIssueHandler);
    assertTrue(result.isPresent());
    var sections = result.get();
    assertEquals(2, sections.size());

    var preparationSection = sections.getFirst();
    assertEquals("Preparation", preparationSection.name());
    var preparationSteps = preparationSection.steps();
    assertEquals(2, preparationSteps.size());
    assertEquals("Preheat the oven to 350 degrees.", preparationSteps.get(0).text());
    assertEquals("Mix the flour and sugar.", preparationSteps.get(1).text());

    var cookingSection = sections.get(1);
    assertEquals("Cooking", cookingSection.name());
    var cookingSteps = cookingSection.steps();
    assertEquals(1, cookingSteps.size());
    assertEquals("Bake for 30 minutes.", cookingSteps.getFirst().text());
  }

  @Test
  void testNormalizeHowToStepsMixedWithSections() {
    String json =
        """
            [
              {
                "@type": "HowToStep",
                "text": "Preheat the oven to 350 degrees."
              },
              {
                "@type": "HowToSection",
                "name": "Preparation",
                "itemListElement": [
                  {
                    "@type": "HowToStep",
                    "text": "Mix the flour and sugar."
                  }
                ]
              },
              {
                "@type": "HowToStep",
                "text": "Bake for 30 minutes."
              }
            ]
            """;
    var input = objectMapper.readTree(json);
    var result = instructionNormalizer.normalize(input, parseIssueHandler);
    assertTrue(result.isPresent());
    var sections = result.get();
    assertEquals(3, sections.size());

    var firstSection = sections.getFirst();
    assertNull(firstSection.name());
    var firstSteps = firstSection.steps();
    assertEquals(1, firstSteps.size());
    assertEquals(0, firstSteps.getFirst().position());
    assertEquals("Preheat the oven to 350 degrees.", firstSteps.getFirst().text());

    var preparationSection = sections.get(1);
    assertEquals("Preparation", preparationSection.name());
    assertEquals(1, preparationSection.position());
    var preparationSteps = preparationSection.steps();
    assertEquals(1, preparationSteps.size());
    assertEquals(0, preparationSteps.getFirst().position());
    assertEquals("Mix the flour and sugar.", preparationSteps.getFirst().text());

    var lastSection = sections.get(2);
    assertNull(lastSection.name());
    assertEquals(2, lastSection.position());
    var lastSteps = lastSection.steps();
    assertEquals(1, lastSteps.size());
    assertEquals("Bake for 30 minutes.", lastSteps.getFirst().text());
  }

  @Test
  void testNormalizeInstructionArrayWithInvalidType() {
    String json =
        """
          [
            {
              "@type": "HowToStep",
              "text": "Preheat the oven to 350 degrees."
            },
            {
              "@type": "InvalidType",
              "text": "This item has an unsupported type."
            },
            {
              "@type": "HowToStep",
              "text": "Bake for 30 minutes."
            }
          ]
          """;
    var input = objectMapper.readTree(json);
    var result = instructionNormalizer.normalize(input, parseIssueHandler);
    assertTrue(result.isPresent());
    var sections = result.get();
    assertEquals(1, sections.size());

    var section = sections.getFirst();
    assertNull(section.name());
    assertEquals(0, section.position());
    var steps = section.steps();
    assertEquals("Preheat the oven to 350 degrees.", steps.getFirst().text());
    assertEquals("Bake for 30 minutes.", steps.get(1).text());
  }

  @Test
  void testNormalizeInvalidInstructionArray() {
    String json =
        """
          [
            "Preheat the oven to 350 degrees.",
            {
              "@type": "InvalidType",
              "text": "This item has an unsupported type."
            }
          ]
          """;
    var input = objectMapper.readTree(json);
    var result = instructionNormalizer.normalize(input, parseIssueHandler);
    assertTrue(result.isEmpty());
  }

  @Test
  void testNormalizeEmptyArray() {
    String json = "[]";
    var input = objectMapper.readTree(json);
    var result = instructionNormalizer.normalize(input, parseIssueHandler);
    assertTrue(result.isEmpty());
    assertEquals(1, parseIssueHandler.toList().size());
  }

  @Test
  void testNormalizeUnsupportedType() {
    String json = "42";
    var input = objectMapper.readTree(json);
    var result = instructionNormalizer.normalize(input, parseIssueHandler);
    assertTrue(result.isEmpty());
  }
}
