package com.hsteinmetz.jstove.model;

/**
 * @author Hendrik Steinmetz
 */
public record CostInfo(String currency, long minValue, long maxValue, Object value) {}
