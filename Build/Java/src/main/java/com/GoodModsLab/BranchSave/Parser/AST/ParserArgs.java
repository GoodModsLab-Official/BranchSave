package com.GoodModsLab.BranchSave.Parser.AST;

import com.GoodModsLab.BranchSave.Exceptions.ParserExceptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ParserArgs {

    private static final Token EOF = new Token(TokenType.EOF, "");

    private final HashMap jsonCompile;
    private final List<Token> tokens;
    private final int size;

    private int pos;

    public ParserArgs(List<Token> tokens, HashMap jsonCompile) {
        this.jsonCompile = jsonCompile;
        this.tokens = tokens;
        size = tokens.size();
    }

    public List<Expression> parse() throws ParserExceptions {
        final List<Expression> result = new ArrayList<>();
        while (!match(TokenType.EOF)) {
            result.add(expression());
        }
        return result;
    }

    private Expression expression() throws ParserExceptions {
        return additive();
    }

    private Expression additive() throws ParserExceptions {
        Expression result = multiplicative();

        while (true) {
            if (match(TokenType.PLUS)) {
                result = new BinaryExpression('+', result, multiplicative());
                continue;
            }
            if (match(TokenType.MINUS)) {
                result = new BinaryExpression('-', result, multiplicative());
                continue;
            }
            break;
        }

        return result;
    }

    private Expression multiplicative() throws ParserExceptions {
        Expression result = unary();

        while (true) {
            if (match(TokenType.MUL)) {
                result = new BinaryExpression('*', result, unary());
                continue;
            }
            if (match(TokenType.DIV)) {
                result = new BinaryExpression('/', result, unary());
                continue;
            }
            break;
        }

        return result;
    }

    private Expression unary() throws ParserExceptions {
        if (match(TokenType.MINUS)) {
            return new UnaryExpression('-', primary());
        }
        if (match(TokenType.PLUS)) {
            return primary();
        }
        return primary();
    }

    private Expression primary() throws ParserExceptions {
        final Token current = get(0);
        if (match(TokenType.NUMBER)) {
            return new NumberExpression(Double.parseDouble(current.getText()));
        }
        if (match(TokenType.WORD)) {
            return new VariablesExpression(current.getText(), jsonCompile);
        }
        if (match(TokenType.L_PAREN)) {
            Expression result = expression();
            match(TokenType.R_PAREN);
            return result;
        }
        throw new ParserExceptions("Unknown expression", "");
    }

    private Token consume(TokenType type) {
        final Token current = get(0);
        if (type != current.getType()) throw new RuntimeException("Token " + current + " doesn't match " + type);
        pos++;
        return current;
    }

    private boolean match(TokenType type) {
        final Token current = get(0);
        if (type != current.getType()) return false;
        pos++;
        return true;
    }

    private Token get(int relativePosition) {
        final int position = pos + relativePosition;
        if (position >= size) return EOF;
        return tokens.get(position);
    }
}
