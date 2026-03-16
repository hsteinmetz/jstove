package com.hsteinmetz.jstove.api;

import com.hsteinmetz.jstove.internal.DefaultFieldNameProvider;

/**
 * @author Hendrik Steinmetz
 */
public class FieldNameProviders {

  public static FieldNameProvider defaultFieldNameProvider() {
    return new DefaultFieldNameProvider();
  }
}
