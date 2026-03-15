package com.hsteinmetz.jstove.model;

/**
 * @author Hendrik Steinmetz
 */
public record MediaRef(String url, String altText) {
  public static MediaRef of(String url, String altText) {
    return new MediaRef(url, altText);
  }
}
