package com.hsteinmetz.jstove.model;

import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author Hendrik Steinmetz
 */
public record DateInfo(
    ZonedDateTime dateCreated, ZonedDateTime dateUpdated, ZonedDateTime datePublished) {}
