package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import java.util.Optional;
import tools.jackson.databind.JsonNode;

public interface GenericNormalizer<T> {

  public Optional<T> normalize(JsonNode input, ParseIssueHandler parseIssueHandler);
}
