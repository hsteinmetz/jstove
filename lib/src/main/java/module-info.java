module jstove.lib.main {
  requires tools.jackson.databind;
  requires static lombok;

  exports com.hsteinmetz.jstove.api;
  exports com.hsteinmetz.jstove.model;
  exports com.hsteinmetz.jstove.api.except;
}
