package com.hsteinmetz.jstove.model;

import tools.jackson.databind.JsonNode;

/**
 * @author Hendrik Steinmetz
 */
public record SourceMetadata(String sourceFormat, String schemaType, JsonNode sourceNode) {}
