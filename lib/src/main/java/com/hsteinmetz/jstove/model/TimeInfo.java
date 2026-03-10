package com.hsteinmetz.jstove.model;

import java.time.Duration;

/**
 * @author Hendrik Steinmetz
 */
public record TimeInfo(Duration prepTime, Duration cookTime, Duration totalTime) {}
