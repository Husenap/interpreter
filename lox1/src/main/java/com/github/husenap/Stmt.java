package com.github.husenap;

import java.util.List;

interface Stmt {
    interface Visitor<R> {
        R visit(Expression stmt);

        R visit(Print stmt);

        R visit(Var stmt);

        R visit(Block stmt);
    }

    abstract <R> R accept(Visitor<R> v);

    record Expression(Expr expr) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> v) {
            return v.visit(this);
        }
    }

    record Print(Expr expr) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> v) {
            return v.visit(this);
        }
    }

    record Var(Token name, Expr initializer) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> v) {
            return v.visit(this);
        }
    }
    record Block(List<Stmt> statements) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> v) {
            return v.visit(this);
        }
    }
}
