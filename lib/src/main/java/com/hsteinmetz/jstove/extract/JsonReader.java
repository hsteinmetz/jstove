package com.hsteinmetz.jstove.extract;

import com.hsteinmetz.jstove.jackson.ObjectMapperFactory;
import java.io.InputStream;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class JsonReader {

  private static final ObjectMapper mapper = ObjectMapperFactory.getInstance().getObjectMapper();

  public JsonNode parse(String raw) {
    try {
      return mapper.readTree(raw);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse JSON input", e);
    }
  }

  public JsonNode parse(InputStream stream) {
    try {
      return mapper.readTree(stream);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse JSON input", e);
    }
  }
}
