package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import com.hsteinmetz.jstove.jackson.ObjectMapperFactory;
import org.junit.jupiter.api.BeforeEach;
import tools.jackson.databind.ObjectMapper;

/**
 * @author Hendrik Steinmetz
 */
public abstract class GenericNormalizerTest {

  protected static final ObjectMapper MAPPER = ObjectMapperFactory.getInstance().getObjectMapper();
  protected static final ParseIssueHandler PARSE_ISSUE_HANDLER =
      new ParseIssueHandler(com.hsteinmetz.jstove.api.ParseOptions.defaultOptions());

  @BeforeEach
  void clearParseIssues() {
    PARSE_ISSUE_HANDLER.clear();
  }
}
