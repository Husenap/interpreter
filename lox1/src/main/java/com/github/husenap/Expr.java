package com.github.husenap;

interface Expr {
  interface Visitor<R> {
    R visit(Assign expr);

    R visit(Binary expr);

    R visit(Unary expr);

    R visit(Literal expr);

    R visit(Grouping expr);

    R visit(Variable expr);
  }

  <R> R accept(Visitor<R> v);

  record Assign(Token name, Expr value) implements Expr {
    @Override
    public <R> R accept(Visitor<R> v) {
      return v.visit(this);
    }
  }
  record Binary(Expr left, Token operator, Expr right) implements Expr {
    @Override
    public <R> R accept(Visitor<R> v) {
      return v.visit(this);
    }
  }

  record Unary(Token operator, Expr right) implements Expr {
    @Override
    public <R> R accept(Visitor<R> v) {
      return v.visit(this);
    }
  }

  record Literal(Object value) implements Expr {
    @Override
    public <R> R accept(Visitor<R> v) {
      return v.visit(this);
    }
  }

  record Grouping(Expr expr) implements Expr {
    @Override
    public <R> R accept(Visitor<R> v) {
      return v.visit(this);
    }
  }

  record Variable(Token name) implements Expr {
    @Override
    public <R> R accept(Visitor<R> v) {
      return v.visit(this);
    }
  }
}