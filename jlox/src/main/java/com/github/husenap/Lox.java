package com.github.husenap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Lox {
  static boolean hadError = false;
  static boolean hadRuntimeError = false;

  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.out.println("Usage: jlox [script]");
      System.exit(64);
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }

  private static void runFile(String path) throws IOException {
    String script = new ScriptReader().read(path);
    run(script);
    if (hadError)
      System.exit(65);
    if (hadRuntimeError)
      System.exit(70);
  }

  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    while (true) {
      System.out.print("\n> ");
      String line = reader.readLine();
      if (line == null)
        break;
      run(line);
      hadError = false;
    }
  }

  private static void run(String source) {
    Lexer scanner = new Lexer(source);
    List<Token> tokens = scanner.scanTokens();

    Parser parser = new Parser(tokens);
    List<Stmt> stmts = parser.parse();

    if (hadError)
      return;

    Interpreter interpreter = new Interpreter();
    Resolver resolver = new Resolver(interpreter);
    resolver.resolve(stmts);

    if (hadError)
      return;

    interpreter.interpret(stmts);
  }

  public static void error(int line, String message) {
    report(line, "", message);
  }

  public static void error(Token token, String message) {
    if (token.type == TokenType.EOF) {
      report(token.line, " at end", message);
    } else {
      report(token.line, String.format(" at '%s'", token.lexeme), message);
    }
  }

  public static void runtimeError(RuntimeError error) {
    System.err.println(String.format("[line %d] Runtime Error: %s", error.token.line, error.getMessage()));
    hadRuntimeError = true;
  }

  private static void report(int line, String where, String message) {
    System.out.println(String.format("[line %d] Error%s: %s", line, where, message));
    hadError = true;
  }
}
