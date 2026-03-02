package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.api.ParseOptions;
import java.util.Optional;

import com.hsteinmetz.jstove.internal.WarningCollector;
import tools.jackson.databind.JsonNode;

public interface GenericNormalizer<T> {

  public Optional<T> normalize(
      JsonNode input, ParseOptions options, WarningCollector warningCollector);
}
