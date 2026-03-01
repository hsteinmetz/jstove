package com.hsteinmetz.jstove.model;

import tools.jackson.databind.JsonNode;

public record SourceMetadata(String sourceFormat, String schemaType, JsonNode sourceNode) {}
