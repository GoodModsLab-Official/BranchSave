package com.GoodModsLab.BranchSave.Parser;

import com.GoodModsLab.BranchSave.Exceptions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ParserArgs {

    public static HashMap<String, Function> functions = getFunctionMap();
    public static HashMap jsonCompile;

    public  ParserArgs(HashMap jsonCompile) {
        this.jsonCompile = jsonCompile;
    }

    public List<Token> parse(String context) throws ParserExceptions {
        int pos = 0;
        ArrayList<Token> tokens = new ArrayList<>();

        while (pos < context.length()) {
            char current = context.charAt(pos);

            switch (current) {
                case '(':
                    tokens.add(new Token(TokenType.L_PARENT, current));
                    pos++;
                    continue;
                case ')':
                    tokens.add(new Token(TokenType.R_PARENT, current));
                    pos++;
                    continue;
                case '+':
                    tokens.add(new Token(TokenType.PLUS, current));
                    pos++;
                    continue;
                case '-':
                    tokens.add(new Token(TokenType.MINUS, current));
                    pos++;
                    continue;
                case '*':
                    tokens.add(new Token(TokenType.MUL, current));
                    pos++;
                    continue;
                case '/':
                    tokens.add(new Token(TokenType.DIV, current));
                    pos++;
                    continue;
                case ',':
                    tokens.add(new Token(TokenType.COMMA, current));
                    pos++;
                    continue;
                default:
                    if (current <= '9' && current >= '0') {
                        StringBuilder buffer = new StringBuilder();
                        do {
                            buffer.append(current);
                            pos++;
                            if (pos >= context.length()) {
                                break;
                            }
                            current = context.charAt(pos);
                        } while (current <= '9' && current >= '0');
                        tokens.add(new Token(TokenType.NUMBER, buffer.toString()));
                    } else {
                        if (current != ' ') {
                            if (current >= 'a' && current <= 'z' || current >= 'A' && current <= 'Z') {
                                StringBuilder buffer = new StringBuilder();
                                do {
                                    buffer.append(current);
                                    pos++;
                                    if (pos >= context.length()) {
                                        break;
                                    }
                                    current = context.charAt(pos);
                                } while (current >= 'a' && current <= 'z' || current >= 'A' && current <= 'Z');

                                if (functions.containsKey(buffer.toString())) {
                                    tokens.add(new Token(TokenType.WORD, buffer.toString()));
                                } else if (jsonCompile.containsKey(buffer.toString())) {
                                    tokens.add(new Token(TokenType.VARIABLE, buffer.toString()));
                                } else {
                                    throw new ParserExceptions("Unexpected function: ", buffer.toString());
                                }
                            }
                        } else {
                            pos++;
                        }
                    }
            }
        }
        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    public static double expression(TokenBuffer tokens) throws ParserExceptions {
        Token token = tokens.next();
        if (token.type == TokenType.EOF) {
            return 0;
        } else {
            tokens.back();
            return additive(tokens);
        }
    }

    public static double additive(TokenBuffer tokens) throws ParserExceptions {
        double value = muldiv(tokens);
        while (true) {
            Token token = tokens.next();
            switch (token.type) {
                case PLUS:
                    value += muldiv(tokens);
                    break;
                case MINUS:
                    value -= muldiv(tokens);
                    break;
                case EOF:
                case R_PARENT:
                case COMMA:
                    tokens.back();
                    return value;
                default:
                    throw new RuntimeException("Unexpected token: " + token.context
                            + " at position: " + tokens.getPos());
            }
        }
    }

    public static double muldiv(TokenBuffer tokens) throws ParserExceptions {
        double value = factor(tokens);
        while (true) {
            Token token = tokens.next();
            switch (token.type) {
                case MUL:
                    value *= factor(tokens);
                    break;
                case DIV:
                    value /= factor(tokens);
                    break;
                case EOF:
                case R_PARENT:
                case COMMA:
                case PLUS:
                case MINUS:
                    tokens.back();
                    return value;
                default:
                    throw new RuntimeException("Unexpected token: " + token.context
                            + " at position: " + tokens.getPos());
            }
        }
    }

    public static double factor(TokenBuffer tokens) throws ParserExceptions {
        Token token = tokens.next();
        switch (token.type) {
            case VARIABLE:
                tokens.back();
                return  variables(tokens, jsonCompile);
            case WORD:
                tokens.back();
                return function(tokens);
            case MINUS:
                double value = factor(tokens);
                return -value;
            case NUMBER:
                return Double.parseDouble(token.context);
            case L_PARENT:
                value = expression(tokens);
                token = tokens.next();
                if (token.type != TokenType.R_PARENT) {
                    throw new ParserExceptions("Missing ')' in ", token.context + " position: " + tokens.getPos());
                }
                return value;
            default:
                throw new ParserExceptions("Unknown ", token.context + " position: " + tokens.getPos());

        }
    }

    public static double function(TokenBuffer tokens) throws ParserExceptions {
        String name = TokenBuffer.next().context;
        Token token = tokens.next();

        if (token.type != TokenType.L_PARENT) {
            throw new ParserExceptions("Invalid syntax in ", token.context);
        }

        ArrayList<Double> args = new ArrayList<>();
        token = tokens.next();
        if (token.type != TokenType.R_PARENT) {
            tokens.back();
            do {
                args.add(expression(tokens));
                token = tokens.next();
                if (token.type != TokenType.COMMA && token.type != TokenType.R_PARENT) {
                    throw new ParserExceptions("Invalid syntax in ", token.context);
                }
            } while (token.type == TokenType.COMMA);
        }
        return functions.get(name).apply(args);
    }

    public static double variables(TokenBuffer tokens, HashMap jsonCompile) {
        String name = TokenBuffer.next().context;
        double value = Double.valueOf(jsonCompile.get(name).toString());
        return value;
    }


    public static HashMap<String, Function> getFunctionMap() {
        HashMap<String, Function> functions = new HashMap<>();
        functions.put("min", args -> {
            if (args.isEmpty()) {
                throw new ParserExceptions("Expected operators in the function", "");
            }
            double min = args.get(0);
            for (Double val: args) {
                if (val < min) {
                    min = val;
                }
            }
            return min;
        });
        functions.put("pow", args -> {
            if (args.size() != 2) {
                throw new ParserExceptions("Expected two operators in the function", "");
            }
            return Math.pow(args.get(0), args.get(1));
        });
        return functions;
    }
}
