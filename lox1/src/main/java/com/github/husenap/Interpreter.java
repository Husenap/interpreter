package com.github.husenap;

import java.util.List;

import com.github.husenap.Expr.*;
import com.github.husenap.Stmt.*;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    private Environment environment = new Environment();

    Object interpret(Expr expr) {
        Object result = null;
        try {
            result = evaluate(expr);
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
        return result;
    }

    void interpret(List<Stmt> statements) {
        try {
            for (Stmt stmt : statements) {
                execute(stmt);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    @Override
    public Object visit(Binary expr) {
        Object left = evaluate(expr.left());
        Object right = evaluate(expr.right());

        switch (expr.operator().type) {
        case MINUS:
            checkNumberOperands(expr.operator(), left, right);
            return (double) left - (double) right;
        case SLASH:
            checkNumberOperands(expr.operator(), left, right);
            return (double) left / (double) right;
        case STAR:
            checkNumberOperands(expr.operator(), left, right);
            return (double) left * (double) right;
        case PLUS:
            if (left instanceof Double && right instanceof Double) {
                return (double) left + (double) right;
            } else if (left instanceof String && right instanceof String) {
                return (String) left + (String) right;
            }
            throw new RuntimeError(expr.operator(), "Operands must be two numbers or two strings.");
        case GREATER:
            checkNumberOperands(expr.operator(), left, right);
            return (double) left > (double) right;
        case GREATER_EQUAL:
            checkNumberOperands(expr.operator(), left, right);
            return (double) left >= (double) right;
        case LESS:
            checkNumberOperands(expr.operator(), left, right);
            return (double) left < (double) right;
        case LESS_EQUAL:
            checkNumberOperands(expr.operator(), left, right);
            return (double) left <= (double) right;
        case BANG_EQUAL:
            return !isEqual(left, right);
        case EQUAL_EQUAL:
            return isEqual(left, right);
        default:
        }
        return null;
    }

    @Override
    public Object visit(Unary expr) {
        Object right = evaluate(expr.right());

        switch (expr.operator().type) {
        case BANG:
            return !isTruthy(right);
        case MINUS:
            checkNumberOperands(expr.operator(), right);
            return -(double) right;
        default:
        }

        return null;
    }

    @Override
    public Object visit(Literal expr) {
        return expr.value();
    }

    @Override
    public Object visit(Grouping expr) {
        return evaluate(expr.expr());
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private boolean isTruthy(Object object) {
        if (object == null)
            return false;
        if (object instanceof Boolean)
            return (boolean) object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null)
            return true;
        if (a == null)
            return false;
        return a.equals(b);
    }

    private void checkNumberOperands(Token operator, Object... operands) {
        for (Object operand : operands) {
            if (!(operand instanceof Double))
                throw new RuntimeError(operator, "Operand must be a number.");
        }
    }

    @Override
    public Void visit(Expression stmt) {
        evaluate(stmt.expr());
        return null;
    }

    @Override
    public Void visit(Print stmt) {
        Object value = evaluate(stmt.expr());
        System.out.println(stringify(value));
        return null;
    }

    private String stringify(Object value) {
        if (value == null)
            return "nil";

        String text = value.toString();

        if (value instanceof Double && text.endsWith(".0")) {
            text = text.substring(0, text.length() - 2);
        }

        return text;
    }

    @Override
    public Void visit(Var stmt) {
        Object value = null;
        if (stmt.initializer() != null) {
            value = evaluate(stmt.initializer());
        }

        environment.define(stmt.name().lexeme, value);
        return null;
    }

    @Override
    public Object visit(Variable expr) {
        return environment.get(expr.name());
    }

    @Override
    public Object visit(Assign expr) {
        Object value = evaluate(expr.value());
        environment.assign(expr.name(), value);
        return value;
    }

    @Override
    public Void visit(Block stmt) {
        executeBlock(stmt.statements(), new Environment(environment));
        return null;
    }

    void executeBlock(List<Stmt> stmts, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt stmt : stmts) {
                execute(stmt);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void visit(If stmt) {
        if (isTruthy(evaluate(stmt.condition()))) {
            execute(stmt.thenBranch());
        } else if (stmt.elseBranch() != null) {
            execute(stmt.elseBranch());
        }
        return null;
    }

    @Override
    public Object visit(Logical expr) {
        Object left = evaluate(expr.left());

        if (expr.operator().type == TokenType.OR) {
            if (isTruthy(left))
                return left;
        } else {
            if (!isTruthy(left))
                return left;
        }
        return evaluate(expr.right());
    }
}
