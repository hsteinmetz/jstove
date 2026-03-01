package com.hsteinmetz.jstove.api;

/**
 * Enumeration representing the parsing mode for recipe parsing. This enum defines two modes:
 * LENIENT and STRICT. The LENIENT mode allows for more flexible parsing, tolerating certain
 * deviations from the expected format, while the STRICT mode enforces a stricter adherence to the
 * expected format, rejecting any input that does not conform to it.
 */
public enum ParseMode {
  LENIENT,
  STRICT
}
