package com.github.husenap;

public class Test {
    public static void main(String[] args) {
        Expr expr = new Binary(new Unary(new Token(TokenType.MINUS, "-", null, 1), new Literal(123)),
                new Token(TokenType.STAR, "*", null, 1), new Grouping(new Literal(45.67)));
        System.out.println(new AstPrinter().print(expr));
    }
}
