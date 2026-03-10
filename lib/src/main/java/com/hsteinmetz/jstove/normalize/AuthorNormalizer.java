package com.hsteinmetz.jstove.normalize;

import com.hsteinmetz.jstove.api.except.RecipeParseErrorCode;
import com.hsteinmetz.jstove.extract.FieldReader;
import com.hsteinmetz.jstove.internal.ParseIssueHandler;
import com.hsteinmetz.jstove.model.AuthorInfo;
import com.hsteinmetz.jstove.normalize.util.NormalizationUtils;
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
    if (NormalizationUtils.isNullOrEmptyNode(authorNode)) return Optional.empty();

    if (authorNode.isArray()) {
      List<AuthorInfo> authors = new ArrayList<>();
      for (JsonNode author : authorNode) {
        if (author.isObject()) {
          authors.add(constructFromObject(author));
        } else if (author.isString()) {
          authors.add(new AuthorInfo(author.asString(), null, null));
        } else {
          parseIssueHandler.warnOrThrow(
              RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
              "author",
              "Unsupported shape for author field; expected object, string or array",
              JsonPointer.compile("/author"));
        }
      }
      return Optional.of(authors);
    } else if (authorNode.isObject()) {
      AuthorInfo author = constructFromObject(authorNode);
      return Optional.of(List.of(author));
    } else if (authorNode.isString()) {
      AuthorInfo author = new AuthorInfo(authorNode.asString(), null, null);
      return Optional.of(List.of(author));
    } else {
      parseIssueHandler.warnOrThrow(
          RecipeParseErrorCode.FIELD_UNSUPPORTED_SHAPE,
          "author",
          "Unsupported shape for author field; expected object, string or array",
          JsonPointer.compile("/author"));
    }

    return Optional.empty();
  }

  private AuthorInfo constructFromObject(JsonNode authorNode) {
    String name = reader.readAsText(authorNode, "name").orElse(null);
    String url = reader.readAsText(authorNode, "url").orElse(null);
    String email = reader.readAsText(authorNode, "email").orElse(null);

    return new AuthorInfo(name, email, url);
  }
}
