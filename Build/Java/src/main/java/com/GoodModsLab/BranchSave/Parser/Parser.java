package com.GoodModsLab.BranchSave.Parser;

import com.GoodModsLab.BranchSave.Exceptions.ParserExceptions;
import com.GoodModsLab.BranchSave.Parser.AST.Expression;
import com.GoodModsLab.BranchSave.Parser.AST.LexerArgs;
import com.GoodModsLab.BranchSave.Parser.AST.ParserArgs;
import com.GoodModsLab.BranchSave.Parser.AST.Token;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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
            if (keyBuffer.equals("$print")) {
                System.out.println(tokenizeArgs(valueBuffer));
            }
        } else {
            jsonCompile.put(keyBuffer, tokenizeArgs(valueBuffer));

        }
        return jsonCompile;
    }

    private String tokenizeArgs(String context) throws ParserExceptions {

        // Import variables
        if (context.trim().contains("($)")) {
            if (jsonCompile.containsKey(context.trim().replace("($)", ""))) {
                return jsonCompile.get(context.trim().replace("($)", "")).toString();
            } else {
                throw new ParserExceptions("Unknown memory location or variable on the argument ", context.trim());
            }
        }

        // Operators
        if (context.trim().contains("($len)")) {
            if (jsonCompile.containsKey(context.trim().replace("($len)", ""))) {
                String result = jsonCompile.get(context.trim().replace("($len)", "")).toString();
                return String.valueOf(result.length());
            } else {
                return String.valueOf(context.trim().replace("($len)", "").length());
            }
        }

        if (context.trim().contains("($trim)")) {
            if (jsonCompile.containsKey(context.trim().replace("($trim)", ""))) {
                String result = jsonCompile.get(context.trim().replace("($trim)", "")).toString();
                return result.trim();
            } else {
                return context.trim().replace("($trim)", "").trim();
            }
        }

        if (context.trim().contains("($toUpperCase)")) {
            if (jsonCompile.containsKey(context.trim().replace("($toUpperCase)", ""))) {
                String result = jsonCompile.get(context.trim().replace("($toUpperCase)", "")).toString();
                return result.toUpperCase();
            } else {
                return context.trim().replace("($toUpperCase)", "").toUpperCase();
            }
        }

        if (context.trim().contains("($toLowerCase)")) {
            if (jsonCompile.containsKey(context.trim().replace("($toLowerCase)", ""))) {
                String result = jsonCompile.get(context.trim().replace("($toLowerCase)", "")).toString();
                return result.toLowerCase();
            } else {
                return context.trim().replace("($toLowerCase)", "").toLowerCase();
            }
        }

        // Mathematics blocks
        if (context.trim().contains("($math)")) {
            String result = "";
            List<Token> tokens = new LexerArgs(context.trim().replace("($math)", "")).ParseToTokens();
            List<Expression> expressions = new ParserArgs(tokens, jsonCompile).parse();
            for (Expression expr : expressions) {
                result = String.valueOf(expr.eval());
            }
           return result;
        }

        if (context.trim().contains("($round)")) {
            if (jsonCompile.containsKey(context.trim().replace("($round)", ""))) {
                String result = jsonCompile.get(context.trim().replace("($round)", "")).toString();
                return String.valueOf(Math.round(Double.parseDouble(result)));
            } else {
                return String.valueOf(Math.round(Double.parseDouble(context.trim().replace("($round)", ""))));
            }
        }

        if (context.trim().contains("($ceil)")) {
            if (jsonCompile.containsKey(context.trim().replace("($ceil)", ""))) {
                String result = jsonCompile.get(context.trim().replace("($ceil)", "")).toString();
                return String.valueOf(Math.ceil(Double.parseDouble(result)));
            } else {
                return String.valueOf(Math.ceil(Double.parseDouble(context.trim().replace("($ceil)", ""))));
            }
        }

        if (context.trim().contains("($floor)")) {
            if (jsonCompile.containsKey(context.trim().replace("($floor)", ""))) {
                String result = jsonCompile.get(context.trim().replace("($floor)", "")).toString();
                return String.valueOf(Math.floor(Double.parseDouble(result)));
            } else {
                return String.valueOf(Math.floor(Double.parseDouble(context.trim().replace("($floor)", ""))));
            }
        }

        if (context.trim().contains("($sin)")) {
            if (jsonCompile.containsKey(context.trim().replace("($sin)", ""))) {
                String result = jsonCompile.get(context.trim().replace("($sin)", "")).toString();
                return String.valueOf(Math.sin(Double.parseDouble(result)));
            } else {
                return String.valueOf(Math.sin(Double.parseDouble(context.trim().replace("($sin)", ""))));
            }
        }

        if (context.trim().contains("($cos)")) {
            if (jsonCompile.containsKey(context.trim().replace("($cos)", ""))) {
                String result = jsonCompile.get(context.trim().replace("($cos)", "")).toString();
                return String.valueOf(Math.cos(Double.parseDouble(result)));
            } else {
                return String.valueOf(Math.cos(Double.parseDouble(context.trim().replace("($cos)", ""))));
            }
        }

        if (context.trim().contains("($tan)")) {
            if (jsonCompile.containsKey(context.trim().replace("($tan)", ""))) {
                String result = jsonCompile.get(context.trim().replace("($tan)", "")).toString();
                return String.valueOf(Math.tan(Double.parseDouble(result)));
            } else {
                return String.valueOf(Math.tan(Double.parseDouble(context.trim().replace("($tan)", ""))));
            }
        }

        if (context.trim().contains("($asin)")) {
            if (jsonCompile.containsKey(context.trim().replace("($asin)", ""))) {
                String result = jsonCompile.get(context.trim().replace("($asin)", "")).toString();
                return String.valueOf(Math.asin(Double.parseDouble(result)));
            } else {
                return String.valueOf(Math.asin(Double.parseDouble(context.trim().replace("($asin)", ""))));
            }
        }

        if (context.trim().contains("($acos)")) {
            if (jsonCompile.containsKey(context.trim().replace("($acos)", ""))) {
                String result = jsonCompile.get(context.trim().replace("($acos)", "")).toString();
                return String.valueOf(Math.acos(Double.parseDouble(result)));
            } else {
                return String.valueOf(Math.acos(Double.parseDouble(context.trim().replace("($acos)", ""))));
            }
        }

        if (context.trim().contains("($atan)")) {
            if (jsonCompile.containsKey(context.trim().replace("($atan)", ""))) {
                String result = jsonCompile.get(context.trim().replace("($atan)", "")).toString();
                return String.valueOf(Math.atan(Double.parseDouble(result)));
            } else {
                return String.valueOf(Math.atan(Double.parseDouble(context.trim().replace("($atan)", ""))));
            }
        }

        if (context.trim().contains("($exp)")) {
            if (jsonCompile.containsKey(context.trim().replace("($exp)", ""))) {
                String result = jsonCompile.get(context.trim().replace("($exp)", "")).toString();
                return String.valueOf(Math.exp(Double.parseDouble(result)));
            } else {
                return String.valueOf(Math.exp(Double.parseDouble(context.trim().replace("($exp)", ""))));
            }
        }

        if (context.trim().contains("($log)")) {
            if (jsonCompile.containsKey(context.trim().replace("($log)", ""))) {
                String result = jsonCompile.get(context.trim().replace("($log)", "")).toString();
                return String.valueOf(Math.log(Double.parseDouble(result)));
            } else {
                return String.valueOf(Math.log(Double.parseDouble(context.trim().replace("($log)", ""))));
            }
        }

        if (context.trim().contains("($log10)")) {
            if (jsonCompile.containsKey(context.trim().replace("($log10)", ""))) {
                String result = jsonCompile.get(context.trim().replace("($log10)", "")).toString();
                return String.valueOf(Math.log10(Double.parseDouble(result)));
            } else {
                return String.valueOf(Math.log10(Double.parseDouble(context.trim().replace("($log10)", ""))));
            }
        }

        if (context.trim().contains("($toRad)")) {
            if (jsonCompile.containsKey(context.trim().replace("($toRad)", ""))) {
                String result = jsonCompile.get(context.trim().replace("($toRad)", "")).toString();
                return String.valueOf(Math.toRadians(Double.parseDouble(result)));
            } else {
                return String.valueOf(Math.toRadians(Double.parseDouble(context.trim().replace("($toRad)", ""))));
            }
        }

        if (context.trim().contains("($toDeg)")) {
            if (jsonCompile.containsKey(context.trim().replace("($toDeg)", ""))) {
                String result = jsonCompile.get(context.trim().replace("($toDeg)", "")).toString();
                return String.valueOf(Math.toDegrees(Double.parseDouble(result)));
            } else {
                return String.valueOf(Math.toDegrees(Double.parseDouble(context.trim().replace("($toDeg)", ""))));
            }
        }

        if (context.trim().contains("($rand)")) {
            double min = 0;
            double max = 5;
            Random rand = new Random();
            String valueCache = context.trim().replace("($rand)", "");
            String[] randParty = valueCache.trim().split(" TO ");

            if (jsonCompile.containsKey(randParty[0])) {
                min = Double.parseDouble(jsonCompile.get(randParty[0]).toString());
            } else if (jsonCompile.containsKey(randParty[1])){
                max = Double.parseDouble(jsonCompile.get(randParty[1]).toString());
            } else {
                min = Double.parseDouble(randParty[0]);
                max = Double.parseDouble(randParty[1]);
            }

            double random = ThreadLocalRandom.current().nextDouble(min, max);
            return String.valueOf(random);
        }

        // Default block
        return context.trim();
    }



    public double randomize(double max, double min) {
        double r = Math.random();
        if (r < 0.5) {
            return ((1 - Math.random()) * (max - min) + min);
        }
        return (Math.random() * (max - min) + min);
    }
}
