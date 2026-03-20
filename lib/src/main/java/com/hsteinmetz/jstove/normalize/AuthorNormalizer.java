package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import com.hsteinmetz.jstove.model.AuthorInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import tools.jackson.core.JsonPointer;
import tools.jackson.databind.JsonNode;

/**
 * @author Hendrik Steinmetz
 */
public class AuthorNormalizer extends GenericNormalizer<List<AuthorInfo>> {

  public AuthorNormalizer(FieldReader reader) {
    super(reader);
  }

  public Optional<List<AuthorInfo>> normalize(
      JsonNode authorNode, ParseIssueHandler parseIssueHandler) {
    if (isBlank(authorNode)) return Optional.empty();

    return switch (authorNode.getNodeType()) {
      case ARRAY -> normalizeArray(authorNode, parseIssueHandler);
      case OBJECT -> {
        AuthorInfo info = constructFromObject(authorNode);
        yield Optional.of(List.of(info));
      }
      case STRING -> {
        AuthorInfo info = new AuthorInfo(authorNode.asString(), null, null);
        yield Optional.of(List.of(info));
      }
      default ->
          fail(
              parseIssueHandler,
              RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
              "author",
              "Unsupported shape for author field; expected object, string or array");
    };
  }

  private Optional<List<AuthorInfo>> normalizeArray(
      JsonNode authorNode, ParseIssueHandler parseIssueHandler) {
    List<AuthorInfo> authors = new ArrayList<>();
    for (JsonNode author : authorNode) {
      switch (author.getNodeType()) {
        case OBJECT -> authors.add(constructFromObject(author));
        case STRING -> authors.add(new AuthorInfo(author.asString(), null, null));
        default ->
            parseIssueHandler.warnOrThrow(
                RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
                "author",
                "Unsupported shape for author field; expected object, string or array",
                JsonPointer.compile("/author"));
      }
    }
    return Optional.of(authors);
  }

  private AuthorInfo constructFromObject(JsonNode authorNode) {
    String name = text(authorNode, "name").orElse(null);
    String url = text(authorNode, "url").orElse(null);
    String email = text(authorNode, "email").orElse(null);

    return new AuthorInfo(name, email, url);
  }
}
