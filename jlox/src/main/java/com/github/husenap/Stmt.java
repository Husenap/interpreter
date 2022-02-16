package com.github.husenap;

import java.util.List;

interface Stmt {
    interface Visitor<R> {
        R visit(Expression stmt);

        R visit(Var stmt);

        R visit(Block stmt);

        R visit(If stmt);

        R visit(While stmt);

        R visit(Function stmt);

        R visit(Return stmt);
    }

    abstract <R> R accept(Visitor<R> v);

    record Expression(Expr expr) implements Stmt {
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
    record If(Expr condition, Stmt thenBranch, Stmt elseBranch) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> v) {
            return v.visit(this);
        }
    }
    record While(Expr condition, Stmt body) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> v) {
            return v.visit(this);
        }
    }
    record Function(Token name, List<Token> params, List<Stmt> body) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> v) {
            return v.visit(this);
        }
    }
    record Return(Token keyword, Expr value) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> v) {
            return v.visit(this);
        }
    }
}
