package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.extract.JsonReader;
import com.hsteinmetz.jstove.jackson.ObjectMapperFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class RecipeNodeLocatorTest {

  private static final ObjectMapper mapper = ObjectMapperFactory.getInstance().getObjectMapper();

  @Test
  public void testLocate() throws IOException {
    RecipeNodeLocator locator = new RecipeNodeLocator();
    InputStream is = getClass().getResourceAsStream("/recipes.json");
    JsonReader input = new JsonReader();
    JsonNode root = input.parse(is);

    List<JsonNode> nodes = locator.locate(root);
    Assertions.assertEquals(10, nodes.size());
  }
}
