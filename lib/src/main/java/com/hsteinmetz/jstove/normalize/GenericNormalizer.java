package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import java.util.Optional;
import tools.jackson.databind.JsonNode;

/**
 * @author Hendrik Steinmetz
 */
public abstract class GenericNormalizer<T> {

  protected final FieldReader reader;

  public GenericNormalizer(FieldReader reader) {
    this.reader = reader;
  }

  public abstract Optional<T> normalize(JsonNode input, ParseIssueHandler parseIssueHandler);
}
