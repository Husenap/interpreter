package com.github.husenap.natives;

import java.util.List;
import java.util.function.BiFunction;

import com.github.husenap.Environment;
import com.github.husenap.Interpreter;
import com.github.husenap.LoxCallable;

public abstract class Natives {
    private final Environment environment;

    public Natives(Environment environment) {
        this.environment = environment;
    }

    protected void define(String name, int arity, BiFunction<Interpreter, List<Object>, Object> function) {
        environment.define(name, new LoxCallable() {
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return function.apply(interpreter, arguments);
            }

            @Override
            public int arity() {
                return arity;
            }

            @Override
            public String toString() {
                return String.format("<native fn %s>", name);
            }
        });
    }
}
