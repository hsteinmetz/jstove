package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import com.hsteinmetz.jstove.jackson.ObjectMapperFactory;
import org.junit.jupiter.api.BeforeEach;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.JsonNodeFactory;

/**
 * @author Hendrik Steinmetz
 */
public abstract class GenericNormalizerTest {

  protected static final ObjectMapper MAPPER = ObjectMapperFactory.getInstance().getObjectMapper();
  protected static final ParseIssueHandler PARSE_ISSUE_HANDLER =
      new ParseIssueHandler(com.hsteinmetz.jstove.api.ParseOptions.defaultOptions());
  protected static final JsonNodeFactory NODE_FACTORY = JsonNodeFactory.instance;

  @BeforeEach
  void clearParseIssues() {
    PARSE_ISSUE_HANDLER.clear();
  }
}
