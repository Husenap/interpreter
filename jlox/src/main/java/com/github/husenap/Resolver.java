package com.github.husenap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements Stmt.Visitor<Void>, Expr.Visitor<Void> {
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;
    private ClassType currentClass = ClassType.NONE;

    private enum FunctionType {
        NONE, FUNCTION, METHOD, INITIALIZER
    }
    private enum ClassType {
        NONE, CLASS, SUBCLASS
    }

    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    private void endScope() {
        scopes.pop();
    }

    public void resolve(List<Stmt> statements) {
        statements.forEach(this::resolve);
    }

    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void resolve(Expr expr) {
        expr.accept(this);
    }

    private void declare(Token name) {
        if (scopes.isEmpty())
            return;

        var scope = scopes.peek();
        if (scope.containsKey(name.lexeme)) {
            Lox.error(name, "Already a variable with this name in this scope.");
        }
        scope.put(name.lexeme, false);
    }

    private void define(Token name) {
        if (scopes.isEmpty())
            return;
        scopes.peek().put(name.lexeme, true);
    }

    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; --i) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }

    @Override
    public Void visit(Expr.Assign expr) {
        resolve(expr.value());
        resolveLocal(expr, expr.name());
        return null;
    }

    @Override
    public Void visit(Expr.Binary expr) {
        resolve(expr.left());
        resolve(expr.right());
        return null;
    }

    @Override
    public Void visit(Expr.Unary expr) {
        resolve(expr.right());
        return null;
    }

    @Override
    public Void visit(Expr.Logical expr) {
        resolve(expr.left());
        resolve(expr.right());
        return null;
    }

    @Override
    public Void visit(Expr.Literal expr) {
        return null;
    }

    @Override
    public Void visit(Expr.Grouping expr) {
        resolve(expr.expr());
        return null;
    }

    @Override
    public Void visit(Expr.Variable expr) {
        if (!scopes.isEmpty() && scopes.peek().get(expr.name().lexeme) == Boolean.FALSE) {
            Lox.error(expr.name(), "Can't read local variable in its own initializer.");
        }
        resolveLocal(expr, expr.name());
        return null;
    }

    @Override
    public Void visit(Expr.Call expr) {
        resolve(expr.callee());
        expr.arguments().forEach(this::resolve);
        return null;
    }

    @Override
    public Void visit(Stmt.Expression stmt) {
        resolve(stmt.expr());
        return null;
    }

    @Override
    public Void visit(Stmt.Var stmt) {
        declare(stmt.name());
        if (stmt.initializer() != null) {
            resolve(stmt.initializer());
        }
        define(stmt.name());
        return null;
    }

    @Override
    public Void visit(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.statements());
        endScope();
        return null;
    }

    @Override
    public Void visit(Stmt.If stmt) {
        resolve(stmt.condition());
        resolve(stmt.thenBranch());
        if (stmt.elseBranch() != null)
            resolve(stmt.elseBranch());
        return null;
    }

    @Override
    public Void visit(Stmt.While stmt) {
        resolve(stmt.condition());
        resolve(stmt.body());
        return null;
    }

    @Override
    public Void visit(Stmt.Function stmt) {
        declare(stmt.name());
        define(stmt.name());

        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    private void resolveFunction(Stmt.Function stmt, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;
        beginScope();
        for (Token param : stmt.params()) {
            declare(param);
            define(param);
        }
        resolve(stmt.body());
        endScope();
        currentFunction = enclosingFunction;
    }

    @Override
    public Void visit(Stmt.Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            Lox.error(stmt.keyword(), "Can't return from top-level code.");
        }
        if (stmt.value() != null) {
            if (currentFunction == FunctionType.INITIALIZER) {
                Lox.error(stmt.keyword(), "Can't return a value from an initializer.");
            }
            resolve(stmt.value());
        }
        return null;
    }

    @Override
    public Void visit(Stmt.Class stmt) {
        ClassType enclosingClass = currentClass;
        currentClass = ClassType.CLASS;

        declare(stmt.name());
        define(stmt.name());

        if (stmt.superclass() != null && stmt.name().lexeme.equals(stmt.superclass().name().lexeme)) {
            Lox.error(stmt.superclass().name(), "A class can't inherit from itself.");
        }
        if (stmt.superclass() != null) {
            currentClass = ClassType.SUBCLASS;
            resolve(stmt.superclass());
        }
        if (stmt.superclass() != null) {
            beginScope();
            scopes.peek().put("super", true);
        }

        beginScope();
        scopes.peek().put("this", true);

        for (Stmt.Function method : stmt.methods()) {
            FunctionType declaration = FunctionType.METHOD;
            if (method.name().lexeme.equals("init")) {
                declaration = FunctionType.INITIALIZER;
            }
            resolveFunction(method, declaration);
        }

        endScope();
        if (stmt.superclass() != null)
            endScope();

        currentClass = enclosingClass;
        return null;
    }

    @Override
    public Void visit(Expr.Get expr) {
        resolve(expr.object());
        return null;
    }

    @Override
    public Void visit(Expr.Set expr) {
        resolve(expr.value());
        resolve(expr.object());
        return null;
    }

    @Override
    public Void visit(Expr.This expr) {
        if (currentClass == ClassType.NONE) {
            Lox.error(expr.keyword(), "Can't use 'this' outside of a class.");
            return null;
        }
        resolveLocal(expr, expr.keyword());
        return null;
    }

    @Override
    public Void visit(Expr.Super expr) {
        if (currentClass == ClassType.NONE) {
            Lox.error(expr.keyword(), "Can't use 'super' outside of a class.");
        } else if (currentClass != ClassType.SUBCLASS) {
            Lox.error(expr.keyword(), "Can't user 'super' in a class with no superclass.");
        }
        resolveLocal(expr, expr.keyword());
        return null;
    }

    @Override
    public Void visit(Expr.Lambda expr) {
        beginScope();
        for (Token param : expr.params()) {
            declare(param);
            define(param);
        }
        resolve(expr.body());
        endScope();
        return null;
    }

}
