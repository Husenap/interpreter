package com.github.husenap.natives;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.husenap.Environment;

public class NativeFileIO extends Natives {
    public NativeFileIO(Environment environment) {
        super(environment);

        define("read_file", 1, (i, args) -> {
            try {
                byte[] bytes = Files.readAllBytes(Path.of((String) args.get(0)));
                return new String(bytes, Charset.defaultCharset());
            } catch (IOException e) {
                return null;
            }
        });
        define("write_file", 2, (i, args) -> {
            try {
                Files.write(Path.of((String) args.get(0)), ((String) args.get(1)).getBytes());
                return true;
            } catch (IOException e) {
                return false;
            }
        });
    }
}
