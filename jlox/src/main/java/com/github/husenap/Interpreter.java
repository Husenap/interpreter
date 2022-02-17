package com.github.husenap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.husenap.Expr.*;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();

    public Interpreter() {
        globals.define("clock", new LoxCallable() {
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double) System.currentTimeMillis() / 1000.0;
            }

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public String toString() {
                return "<native fn clock>";
            }
        });
        globals.define("tostring", new LoxCallable() {
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return arguments.get(0).toString();
            }

            @Override
            public int arity() {
                return 1;
            }

            @Override
            public String toString() {
                return "<native fn tostring>";
            }
        });

        globals.define("read", new LoxCallable() {
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return System.console().readLine();
            }

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public String toString() {
                return "<native fn read>";
            }
        });
        globals.define("println", new LoxCallable() {
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                System.out.println(arguments.get(0));
                return null;
            }

            @Override
            public int arity() {
                return 1;
            }

            @Override
            public String toString() {
                return "<native fn println>";
            }
        });
        globals.define("print", new LoxCallable() {
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                System.out.print(arguments.get(0));
                return null;
            }

            @Override
            public int arity() {
                return 1;
            }

            @Override
            public String toString() {
                return "<native fn print>";
            }
        });
        globals.define("array_list_create", new LoxCallable() {
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return new ArrayList<Object>();
            }

            @Override
            public int arity() {
                return 0;
            }
        });
        globals.define("array_list_add", new LoxCallable() {
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                if (arguments.get(0) instanceof ArrayList) {
                    ((ArrayList) arguments.get(0)).add(arguments.get(1));
                }
                return null;
            }

            @Override
            public int arity() {
                return 2;
            }
        });
    }

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
    public Void visit(Stmt.Expression stmt) {
        evaluate(stmt.expr());
        return null;
    }

    @Override
    public Void visit(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer() != null) {
            value = evaluate(stmt.initializer());
        }

        environment.define(stmt.name().lexeme, value);
        return null;
    }

    @Override
    public Object visit(Variable expr) {
        return lookUpVariable(expr.name(), expr);
    }

    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
    }

    @Override
    public Object visit(Assign expr) {
        Object value = evaluate(expr.value());

        Integer distance = locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, expr.name(), value);
        } else {
            globals.assign(expr.name(), value);
        }
        return value;
    }

    @Override
    public Void visit(Stmt.Block stmt) {
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
    public Void visit(Stmt.If stmt) {
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

    @Override
    public Void visit(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.condition()))) {
            execute(stmt.body());
        }
        return null;
    }

    @Override
    public Object visit(Call expr) {
        Object callee = evaluate(expr.callee());

        // List<Object> arguments =
        // expr.arguments().stream().map(this::evaluate).toList();
        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments()) {
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof LoxCallable)) {
            throw new RuntimeError(expr.paren(), "Can only call functions and classes.");
        }

        LoxCallable function = (LoxCallable) callee;
        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.paren(),
                    String.format("Expected %d arguments but got %d.", function.arity(), arguments.size()));
        }

        return function.call(this, arguments);
    }

    @Override
    public Void visit(Stmt.Function stmt) {
        LoxFunction function = new LoxFunction(stmt, environment, false);
        environment.define(stmt.name().lexeme, function);
        return null;
    }

    @Override
    public Void visit(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value() != null)
            value = evaluate(stmt.value());
        throw new Return(value);
    }

    public void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    @Override
    public Void visit(Stmt.Class stmt) {
        Object superclass = null;
        if (stmt.superclass() != null) {
            superclass = evaluate(stmt.superclass());
            if (!(superclass instanceof LoxClass)) {
                throw new RuntimeError(stmt.superclass().name(), "Superclass must be a class.");
            }
        }

        environment.define(stmt.name().lexeme, null);

        if (stmt.superclass() != null) {
            environment = new Environment(environment);
            environment.define("super", superclass);
        }

        Map<String, LoxFunction> methods = new HashMap<>();
        for (Stmt.Function method : stmt.methods()) {
            LoxFunction function = new LoxFunction(method, environment, method.name().lexeme.equals("init"));
            methods.put(method.name().lexeme, function);
        }

        LoxClass klass = new LoxClass(stmt.name().lexeme, (LoxClass) superclass, methods);

        if (superclass != null) {
            environment = environment.enclosing;
        }

        environment.assign(stmt.name(), klass);
        return null;
    }

    @Override
    public Object visit(Get expr) {
        Object object = evaluate(expr.object());
        if (object instanceof LoxInstance) {
            return ((LoxInstance) object).get(expr.name());
        }
        throw new RuntimeError(expr.name(), "Only instances have properties.");
    }

    @Override
    public Object visit(Set expr) {
        Object object = evaluate(expr.object());

        if (!(object instanceof LoxInstance)) {
            throw new RuntimeError(expr.name(), "Only instances have fields.");
        }

        Object value = evaluate(expr.value());
        ((LoxInstance) object).set(expr.name(), value);
        return value;
    }

    @Override
    public Object visit(This expr) {
        return lookUpVariable(expr.keyword(), expr);
    }

    @Override
    public Object visit(Super expr) {
        int distance = locals.get(expr);
        LoxClass superclass = (LoxClass) environment.getAt(distance, "super");
        LoxInstance object = (LoxInstance) environment.getAt(distance - 1, "this");
        LoxFunction method = superclass.findMethod(expr.method().lexeme);
        if (method == null) {
            throw new RuntimeError(expr.method(), String.format("Undefined property '%s'.", expr.method().lexeme));
        }
        return method.bind(object);
    }
}
