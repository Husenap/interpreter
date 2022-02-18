package com.github.husenap.natives;

import com.github.husenap.Environment;

public class NativeSystem extends Natives {

    public NativeSystem(Environment environment) {
        super(environment);

        define("clock", 0, (i, args) -> {
            return (double) System.currentTimeMillis() / 1000.0;
        });
        define("tostring", 1, (i, args) -> {
            return args.get(0).toString();
        });
        define("read", 0, (i, args) -> {
            return System.console().readLine();
        });
        define("println", 1, (i, args) -> {
            System.out.println(args.get(0));
            return null;
        });
        define("print", 1, (i, args) -> {
            System.out.print(args.get(0));
            return null;
        });
    }
}
