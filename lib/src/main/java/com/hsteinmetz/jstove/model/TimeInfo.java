package com.hsteinmetz.jstove.model;

import java.time.Duration;

public record TimeInfo(Duration prepTime, Duration cookTime, Duration totalTime) {}
