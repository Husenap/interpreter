package com.github.husenap;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class InterpreterTest {

    Object eval(String source) {
        Scanner scanner = new Scanner(source);
        Parser parser = new Parser(scanner.scanTokens());
        Expr expr = parser.parse();
        return new Interpreter().interpret(expr);
    }

    @Test
    void testMath() {
        assertEquals(8.0, eval("5 + 3"));
        assertEquals(15.0, eval("5 * 3"));
        assertEquals(2.0, eval("5 - 3"));
        assertEquals(5.0 / 3.0, eval("5 / 3"));
        assertEquals(null, eval("5 / \"muffin\""));
    }

    @Test
    void testText() {
        assertEquals("hello, world", eval("\"hello\" + \", world\""));
        assertEquals(null, eval("\"hello\" + 50"));
    }
}
