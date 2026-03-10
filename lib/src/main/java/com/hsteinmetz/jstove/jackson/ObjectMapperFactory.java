package com.hsteinmetz.jstove.jackson;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * @author Hendrik Steinmetz
 */
public class ObjectMapperFactory {

  private static final ObjectMapperFactory INSTANCE = new ObjectMapperFactory();

  private final ObjectMapper objectMapper;

  private ObjectMapperFactory() {
    this.objectMapper = JsonMapper.builder().build();
  }

  public static ObjectMapperFactory getInstance() {
    return INSTANCE;
  }

  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }
}
