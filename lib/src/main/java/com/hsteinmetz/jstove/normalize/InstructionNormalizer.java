package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import com.hsteinmetz.jstove.model.InstructionSection;
import com.hsteinmetz.jstove.model.InstructionStep;
import com.hsteinmetz.jstove.normalize.util.NormalizationUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import tools.jackson.databind.JsonNode;

/**
 * Provides normalization logic for the instruction data in a recipe. For further details, see
 * {@link InstructionNormalizer#normalize(JsonNode, ParseIssueHandler)}
 *
 * @author Hendrik Steinmetz
 */
public class InstructionNormalizer extends GenericNormalizer<List<InstructionSection>> {

  public InstructionNormalizer(FieldReader fieldReader) {
    super(fieldReader);
  }

  /**
   * Normalizes the instruction data from the given JSON node. The method handles various input
   * formats for instructions, including strings, arrays of strings, and structured objects
   * representing sections and steps. It produces a list of {@link InstructionSection} objects that
   * represent the normalized instructions. All errors and warnings encountered during normalization
   * are reported through the provided {@link ParseIssueHandler}.
   *
   * @param input the JSON node containing the instruction data to normalize
   * @param parseIssueHandler the handler for reporting any issues encountered during normalization
   * @return an {@link Optional} containing the list of normalized instruction sections, an empty
   *     {@link Optional} if no valid instructions were found or errors were encountered
   */
  @Override
  public Optional<List<InstructionSection>> normalize(
      JsonNode input, ParseIssueHandler parseIssueHandler) {
    var sections = new ArrayList<InstructionSection>();

    if (NormalizationUtils.isNullOrEmptyNode(input)) return Optional.empty();

    if (input.isString()) {
      var instructions = new InstructionStep(input.asString(), 0, null, Map.of());
      var section = InstructionSection.of(List.of(instructions), 0);
      return Optional.of(List.of(section));
    } else if (input.isArray()) {
      input = input.asArray();
      if (input.isEmpty()) {
        parseIssueHandler.warnOrThrow(
            RecipeParseErrorCode.EMPTY_INSTRUCTION_LIST,
            "@root",
            "Instruction array is empty",
            null);
        return Optional.empty();
      }

      // Case 1: Array of strings
      if (input.valueStream().allMatch(JsonNode::isString)) {
        var result = new ArrayList<InstructionStep>();
        for (int i = 0; i < input.size(); i++) {
          result.add(new InstructionStep(input.get(i).asString(), i, null, Map.of()));
        }

        var section = InstructionSection.of(result, 0);
        return Optional.of(List.of(section));
      } else if (input.valueStream().allMatch(JsonNode::isObject)) {
        var stepBuffer = new ArrayList<InstructionStep>();
        // Case 2: Array with HowToSections and HowToSteps
        for (int i = 0; i < input.size(); i++) {
          var item = input.get(i);

          if (isHowToSection(item)) {
            if (!stepBuffer.isEmpty()) {
              var newSection = InstructionSection.of(new ArrayList<>(stepBuffer), sections.size());
              sections.add(newSection);
              stepBuffer.clear();
            }

            sections.add(normalizeSection(item, sections.size(), parseIssueHandler));
          } else if (isHowToStep(item)) {
            stepBuffer.add(normalizeStep(item, stepBuffer.size(), parseIssueHandler));
          } else {
            parseIssueHandler.warnOrThrow(
                RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
                item.toString(),
                "Instruction array contains an item that is neither a HowToSection nor a HowToStep",
                null);
          }
        }

        if (!stepBuffer.isEmpty()) {
          sections.add(InstructionSection.of(new ArrayList<>(stepBuffer), sections.size()));
        }

        return Optional.of(sections);
      }
    } else {
      parseIssueHandler.warnOrThrow(
          RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
          input.toString(),
          "Instruction node is not a string or an array",
          null);
    }

    return Optional.empty();
  }

  private InstructionStep normalizeStep(
      JsonNode stepNode, int pos, ParseIssueHandler parseIssueHandler) {
    String text = reader.readAsText(stepNode, "text").orElse("");
    String name = reader.readAsText(stepNode, "name").orElse(null);
    return new InstructionStep(text, pos, name, Map.of());
  }

  private InstructionSection normalizeSection(
      JsonNode sectionNode, int pos, ParseIssueHandler parseIssueHandler) {
    String name = reader.readAsText(sectionNode, "name").orElse(null);
    List<InstructionStep> steps = new ArrayList<>();

    if (sectionNode.has("itemListElement") && sectionNode.get("itemListElement").isArray()) {
      var ile = sectionNode.get("itemListElement");
      for (int i = 0; i < ile.size(); i++) {
        var step = (normalizeStep(ile.get(i), i, parseIssueHandler));
        steps.add(step);
      }
    }

    return new InstructionSection(name, steps, pos, Map.of());
  }

  private boolean isHowToSection(JsonNode node) {
    return node.has("@type") && node.get("@type").asString().equalsIgnoreCase("howtosection");
  }

  private boolean isHowToStep(JsonNode node) {
    return node.has("@type") && node.get("@type").asString().equalsIgnoreCase("howtostep");
  }
}
