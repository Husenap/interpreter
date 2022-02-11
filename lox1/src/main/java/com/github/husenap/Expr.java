package com.github.husenap;

interface Expr {
  interface Visitor<E> {
    E visit(Binary expr);

    E visit(Unary expr);

    E visit(Literal expr);

    E visit(Grouping expr);
  }

  <E> E accept(Visitor<E> v);
}

record Binary(Expr left, Token operator, Expr right) implements Expr {
  @Override
  public <E> E accept(Visitor<E> v) {
    return v.visit(this);
  }
}

record Unary(Token operator, Expr right) implements Expr {
  @Override
  public <E> E accept(Visitor<E> v) {
    return v.visit(this);
  }
}

record Literal(Object value) implements Expr {
  @Override
  public <E> E accept(Visitor<E> v) {
    return v.visit(this);
  }
}

record Grouping(Expr expr) implements Expr {
  @Override
  public <E> E accept(Visitor<E> v) {
    return v.visit(this);
  }
}