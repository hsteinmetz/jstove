package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import com.hsteinmetz.jstove.model.InstructionStep;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import tools.jackson.databind.JsonNode;

public class InstructionNormalizer extends GenericNormalizer<List<InstructionStep>> {

  public InstructionNormalizer(FieldReader fieldReader) {
    super(fieldReader);
  }

  @Override
  public Optional<List<InstructionStep>> normalize(
      JsonNode input, ParseIssueHandler parseIssueHandler) {
    var result = new ArrayList<InstructionStep>();

    if (input == null || input.isNull() || input.isMissingNode()) {
      parseIssueHandler.warnOrThrow(
          RecipeParseErrorCode.NO_INSTRUCTION_NODE,
          "@root",
          "No instruction node found; using empty instructions",
          null);
      return Optional.empty();
    }

    if (input.isString()) {
      result.add(new InstructionStep(input.asString(), 1, null, Map.of()));
      return Optional.of(result);
    }

    if (input.isArray()) {
      // Case 1: Array of strings
      // Case 2: Array with HowToSections and HowToSteps
      return Optional.empty();
    }

    return Optional.empty();
  }
}
