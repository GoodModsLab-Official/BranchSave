package com.GoodModsLab.BranchSave.Parser;

import com.GoodModsLab.BranchSave.Exceptions.ParserExceptions;
import com.google.gson.Gson;

import javax.xml.transform.Source;
import java.util.ArrayList;
import java.util.HashMap;

public final class Parser {

    private String keyBuffer = "";
    private String valueBuffer = "";
    private final String context;
    private final HashMap keywords = new HashMap();
    private HashMap jsonCompile;

    public Parser(String context, HashMap jsonCompile) {
        this.context = context;
        this.jsonCompile = jsonCompile;
    }

    public HashMap parser() throws ParserExceptions {
        String bufferToken = context.trim();
        try {
            String[] Source_ = bufferToken.split("\\:");
            keyBuffer = Source_[0].trim();
            valueBuffer = Source_[1].trim();
        } catch (Exception e) {
            try {
                String[] Source_ = bufferToken.split("\\=");
                keyBuffer = Source_[0].trim();
                valueBuffer = Source_[1].trim();
            } catch (Exception e1) {
                keyBuffer = bufferToken.trim();
                valueBuffer = "null";
            }
        }

        if (keyBuffer.substring((int) 0, (int) 1).equals("$")) {
            keywords.put(keyBuffer, valueBuffer);
            if (keyBuffer.equals("$create.Map")) {
                jsonCompile = new HashMap<>();
            }
            if (keyBuffer.equals("$clear.Map")) {
                jsonCompile.clear();
            }
            if (keyBuffer.equals("$add")) {
                jsonCompile.put(valueBuffer.trim(), "null");
            }
            if (keyBuffer.equals("$remove")) {
                if (jsonCompile.containsKey(valueBuffer.trim())) {
                    jsonCompile.remove(valueBuffer.trim());
                } else {
                    throw new ParserExceptions("Unknown memory location or variable on the key ", keyBuffer);
                }
            }
        } else {
            jsonCompile.put(keyBuffer, tokenizeArgs(valueBuffer));

        }
        return jsonCompile;
    }

    private String tokenizeArgs(String context) throws ParserExceptions {
        if (context.trim().contains("($)")) {
            if (jsonCompile.containsKey(context.trim().replace("($)", ""))) {
                return jsonCompile.get(context.trim().replace("($)", "")).toString();
            } else {
                throw new ParserExceptions("Unknown memory location or variable on the argument ", context.trim());
            }
        }
        if (context.trim().contains("($math)")) {
            return String.valueOf(eval(context.trim().replace("($math)", "")));
        }
        return context.trim();
    }

    private double eval(final String _context) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < _context.length()) ? _context.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < _context.length()) throw new RuntimeException("Unexpected: " + (char)ch + " line " + pos);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)` | number
            //        | functionName `(` expression `)` | functionName factor
            //        | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return +parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(_context.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = _context.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')')) throw new RuntimeException("Missing ')' after argument to " + func);
                    } else {
                        x = parseFactor();
                    }
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
}
