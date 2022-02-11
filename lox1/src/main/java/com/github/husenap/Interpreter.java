package com.github.husenap;

import com.github.husenap.Expr.Visitor;

public class Interpreter implements Visitor<Object> {

    Object interpret(Expr expr) {
        Object result = null;
        try {
            result = evaluate(expr);
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
        return result;
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

}
