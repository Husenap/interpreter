package com.github.husenap.natives;

import java.util.ArrayList;
import java.util.List;

import com.github.husenap.Environment;
import com.github.husenap.Interpreter;
import com.github.husenap.LoxCallable;

public class NativeArrayList {
    public static void define(Environment environment) {
        environment.define("__array_list_create", new LoxCallable() {
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return new ArrayList<Object>();
            }

            @Override
            public int arity() {
                return 0;
            }
        });
        environment.define("__array_list_add", new LoxCallable() {
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                if (arguments.get(0) instanceof ArrayList) {
                    ((ArrayList<Object>) arguments.get(0)).add(arguments.get(1));
                }
                return null;
            }

            @Override
            public int arity() {
                return 2;
            }
        });
        environment.define("__array_list_get", new LoxCallable() {
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                if (arguments.get(0) instanceof ArrayList) {
                    return ((ArrayList<Object>) arguments.get(0)).get(((Double) arguments.get(1)).intValue());
                }
                return null;
            }

            @Override
            public int arity() {
                return 2;
            }
        });
        environment.define("__array_list_contains", new LoxCallable() {
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                if (arguments.get(0) instanceof ArrayList) {
                    return ((ArrayList<Object>) arguments.get(0)).contains(arguments.get(1));
                }
                return null;
            }

            @Override
            public int arity() {
                return 2;
            }
        });
        environment.define("__array_list_size", new LoxCallable() {
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                if (arguments.get(0) instanceof ArrayList) {
                    return ((ArrayList<Object>) arguments.get(0)).size();
                }
                return null;
            }

            @Override
            public int arity() {
                return 1;
            }
        });
        environment.define("__array_list_is_empty", new LoxCallable() {
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                if (arguments.get(0) instanceof ArrayList) {
                    return ((ArrayList<Object>) arguments.get(0)).isEmpty();
                }
                return null;
            }

            @Override
            public int arity() {
                return 1;
            }
        });
    }
}
