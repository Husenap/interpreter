package com.github.husenap;

import java.util.List;

interface Expr {
  interface Visitor<R> {
    R visit(Assign expr);

    R visit(Binary expr);

    R visit(Unary expr);

    R visit(Logical expr);

    R visit(Literal expr);

    R visit(Grouping expr);

    R visit(Variable expr);

    R visit(Call expr);

    R visit(Get expr);

    R visit(Set expr);

    R visit(This expr);

    R visit(Super expr);

    R visit(Lambda expr);

    R visit(Subscript expr);

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
  record Logical(Expr left, Token operator, Expr right) implements Expr {
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
  record Call(Expr callee, Token paren, List<Expr> arguments) implements Expr {
    @Override
    public <R> R accept(Visitor<R> v) {
      return v.visit(this);
    }
  }

  record Get(Expr object, Token name) implements Expr {
    @Override
    public <R> R accept(Visitor<R> v) {
      return v.visit(this);
    }
  }

  record Set(Expr object, Token name, Expr value) implements Expr {
    @Override
    public <R> R accept(Visitor<R> v) {
      return v.visit(this);
    }
  }
  record This(Token keyword) implements Expr {
    @Override
    public <R> R accept(Visitor<R> v) {
      return v.visit(this);
    }
  }
  record Super(Token keyword, Token method) implements Expr {
    @Override
    public <R> R accept(Visitor<R> v) {
      return v.visit(this);
    }
  }
  record Lambda(List<Token> params, List<Stmt> body) implements Expr {
    @Override
    public <R> R accept(Visitor<R> v) {
      return v.visit(this);
    }
  }
  record Subscript(Expr object, Expr index) implements Expr {
    @Override
    public <R> R accept(Visitor<R> v) {
      return v.visit(this);
    }
  }
}