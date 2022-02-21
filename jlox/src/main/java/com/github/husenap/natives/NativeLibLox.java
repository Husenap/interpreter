package com.github.husenap.natives;

import com.github.husenap.Environment;

public class NativeLibLox extends Natives {
    public NativeLibLox(Environment environment) {
        super(environment);

        string();
    }

    private void string() {
        define("__string_length", 1, (i, args) -> {
            return (double) ((String) args.get(0)).length();
        });
        define("__string_is_empty", 1, (i, args) -> {
            return ((String) args.get(0)).isEmpty();
        });
        define("__string_char_at", 2, (i, args) -> {
            return ((String) args.get(0)).charAt(toInt(args.get(1)));
        });
        define("__string_substring", 3, (i, args) -> {
            return ((String) args.get(0)).substring(toInt(args.get(1)), toInt(args.get(2)));
        });
    }

}
