package com.github.husenap;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptReader {
    private final Set<Path> seen = new HashSet<>();

    public String read(String filename) {
        Path path = Paths.get(filename);

        return readFile(path.getParent(), path.getFileName());
    }

    private String readFile(Path dir, Path file) {
        Path f = dir.resolve(file);
        if (seen.contains(f))
            return "";
        seen.add(f);

        String text = fileToString(f);

        Pattern p = Pattern.compile("#include \"([^\"]+)\"");
        Matcher m = p.matcher(text);
        text = m.replaceAll(mr -> {
            Path nextPath = dir.resolve(mr.group(1));
            String t = readFile(nextPath.getParent(), nextPath.getFileName());
            return t;
        });

        return text;
    }

    private String fileToString(Path path) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            return new String(bytes, Charset.defaultCharset());
        } catch (IOException e) {
            return "";
        }
    }
}
