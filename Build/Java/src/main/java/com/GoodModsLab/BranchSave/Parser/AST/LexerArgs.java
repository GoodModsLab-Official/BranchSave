package com.GoodModsLab.BranchSave.Parser.AST;

import java.util.ArrayList;
import java.util.List;

public final class LexerArgs {

    private List<Token> tokens;
    private final String context;
    private int length;
    private int pos;

    private static final String OPERATOR_CHARS = "+-*/()";
    private static final TokenType[] OPERATOR_TOKENS = {
            TokenType.PLUS, TokenType.MINUS,
            TokenType.MUL, TokenType.DIV,
            TokenType.L_PAREN, TokenType.R_PAREN,
    };

    public LexerArgs(String context) {
        this.context = context;

        length = context.length();
        tokens = new ArrayList<>();
    }

    public List<Token> ParseToTokens() {
        while (pos < length) {
            final char current = peek(0);
            if (Character.isDigit(current)) tokenizeNumber();
            else if (Character.isLetter(current)) tokenizeWord();
            else if (OPERATOR_CHARS.indexOf(current) != -1) {
                tokenizeOperator();
            } else {
                // whitespaces
                next();
            }
        }
        return tokens;
    }

    private void tokenizeNumber() {
        final StringBuilder buffer = new StringBuilder();
        char current = peek(0);
        while (true) {
            if (current == '.') {
                if (buffer.indexOf(".") != -1) throw new RuntimeException("Invalid float number");
            } else if (!Character.isDigit(current)) {
                break;
            }
            buffer.append(current);
            current = next();
        }
        addToken(TokenType.NUMBER, buffer.toString());
    }

    private void tokenizeWord() {
        final StringBuilder buffer = new StringBuilder();
        char current = peek(0);
        while (true) {
            if (!Character.isLetterOrDigit(current) && (current != '_')  && (current != '$')) {
                break;
            }
            buffer.append(current);
            current = next();
        }
        addToken(TokenType.WORD, buffer.toString());
    }

    private void tokenizeOperator() {
        final int position = OPERATOR_CHARS.indexOf(peek(0));
        addToken(OPERATOR_TOKENS[position]);
        next();
    }


    private char next() {
        pos++;
        return peek(0);
    }

    private char peek(int relativePosition) {
        final int position = pos + relativePosition;
        if (position >= length) return '\0';
        return context.charAt(position);
    }

    private void addToken(TokenType type) {
        addToken(type, "");
    }

    private void addToken(TokenType type, String text) {
        tokens.add(new Token(type, text));
    }
}
