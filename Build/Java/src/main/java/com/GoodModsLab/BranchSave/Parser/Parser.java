package com.GoodModsLab.BranchSave.Parser;

import com.GoodModsLab.BranchSave.Exceptions.ParserExceptions;
import com.google.gson.Gson;

import javax.xml.transform.Source;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Parser {

    private String keyBuffer = "";
    private String valueBuffer = "";
    private final String context;
    private final HashMap keywords = new HashMap();
    private List<Token> tokens = new ArrayList<>();
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
        if (context.trim().contains("($)")) {
            if (jsonCompile.containsKey(context.trim().replace("($)", ""))) {
                return jsonCompile.get(context.trim().replace("($)", "")).toString();
            } else {
                throw new ParserExceptions("Unknown memory location or variable on the argument ", context.trim());
            }
        }
        if (context.trim().contains("($math)")) {
            tokens = new ParserArgs(jsonCompile).parse(context.trim().replace("($math)", ""));
            TokenBuffer tokenBuffer = new TokenBuffer(tokens);
            return String.valueOf(new ParserArgs(jsonCompile).expression(tokenBuffer));
        }
        return context.trim();
    }
}
