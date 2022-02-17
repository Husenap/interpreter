package com.github.husenap.natives;

import java.util.ArrayList;

import com.github.husenap.Environment;

public class NativeArrayList extends Natives {
    public NativeArrayList(Environment environment) {
        super(environment);

        define("__array_list_create", 0, (i, args) -> {
            return new ArrayList<Object>();
        });
        define("__array_list_add", 2, (i, args) -> {
            if (args.get(0) instanceof ArrayList) {
                ((ArrayList<Object>) args.get(0)).add(args.get(1));
            }
            return null;
        });
        define("__array_list_get", 2, (i, args) -> {
            if (args.get(0) instanceof ArrayList) {
                return ((ArrayList<Object>) args.get(0)).get(((Double) args.get(1)).intValue());
            }
            return null;
        });
        define("__array_list_contains", 2, (i, args) -> {
            if (args.get(0) instanceof ArrayList) {
                return ((ArrayList<Object>) args.get(0)).contains(args.get(1));
            }
            return null;
        });
        define("__array_list_size", 1, (i, args) -> {
            if (args.get(0) instanceof ArrayList) {
                return ((ArrayList<Object>) args.get(0)).size();
            }
            return null;
        });
        define("__array_list_is_empty", 1, (i, args) -> {
            if (args.get(0) instanceof ArrayList) {
                return ((ArrayList<Object>) args.get(0)).isEmpty();
            }
            return null;
        });
    }
}
