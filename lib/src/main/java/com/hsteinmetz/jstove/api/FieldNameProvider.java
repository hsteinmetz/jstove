package com.hsteinmetz.jstove.api;

import java.util.List;

/**
 * @author Hendrik Steinmetz
 */
public interface FieldNameProvider {

  List<String> getFieldNamesForType(FieldType type);
}
