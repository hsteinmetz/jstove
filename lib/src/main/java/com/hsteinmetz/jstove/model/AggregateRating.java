package com.hsteinmetz.jstove.model;

/**
 * @author Hendrik Steinmetz
 */
public record AggregateRating(
    int bestRating, int worstRating, float ratingValue, int ratingCount) {}
