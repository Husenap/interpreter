package com.github.husenap;

import com.github.husenap.Expr.Visitor;

public class AstPrinter implements Visitor<String> {
  String print(Expr expr) {
    return expr.accept(this);
  }

  @Override
  public String visit(Binary expr) {
    return parenthesize(expr.operator().lexeme, expr.left(), expr.right());
  }

  @Override
  public String visit(Literal expr) {
    if (expr.value() == null)
      return "nil";
    return expr.value().toString();
  }

  private String parenthesize(String name, Expr... exprs) {
    StringBuilder builder = new StringBuilder();

    builder.append("(").append(name);
    for (Expr expr : exprs) {
      builder.append(" ");
      builder.append(expr.accept(this));
    }
    builder.append(")");

    return builder.toString();
  }

  @Override
  public String visit(Unary expr) {
    return parenthesize(expr.operator().lexeme, expr.right());
  }

  @Override
  public String visit(Grouping expr) {
    return parenthesize("group", expr.expr());
  }
}
