package com.github.husenap;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class AstPrinterTest {
    String eval(String source) {
        Scanner scanner = new Scanner(source);
        Parser parser = new Parser(scanner.scanTokens());
        Expr expr = parser.parse();
        return new AstPrinter().print(expr);
    }

    @Test
    void testArithmetic() {
        assertEquals("(+ 5.0 3.0)", eval("5+3"));
        assertEquals("(+ hello world)", eval("\"hello\" + \"world\""));
    }

    @Test
    void testGrouping() {
        assertEquals("(group (+ 5.0 3.0))", eval("(5+3)"));
    }
}
