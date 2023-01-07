package com.GoodModsLab.BranchSave.Parser;

import com.GoodModsLab.BranchSave.Exceptions.TokenizeExceptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.*;

public final class Lexer {
    private double pos;
    private double pos1;

    private int length;
    private String code;

    private boolean isNextToken;
    private final String context;

    private final ArrayList<String> tokenList = new ArrayList<>();
    public  final HashMap jsonCompile = new HashMap();
    private HashMap compile = new HashMap<>();


    public Lexer(String context) {
        this.context = context;
    }

    public String tokenize() throws TokenizeExceptions {
        pos = 0;
        code = context.trim();
        isNextToken = true;
        try {
            tokenList.addAll(Arrays.asList(code.split(";")));
            for (int i = 0; i < tokenList.size(); i++) {
                compile = new Parser(tokenList.get((int) pos), jsonCompile).parser();
                if (isNextToken) {
                    pos++;
                }
            }
            return new Compile(compile).toJson();
        } catch (Exception e) {
            throw new TokenizeExceptions(String.valueOf(e));
        }
    }

    public String tokenizeJson() throws TokenizeExceptions {
        pos = 0;
        isNextToken = true;
        code = context.trim();
        StringBuilder buffer = new StringBuilder();
        try {
            compile = new Compile().fromJson(code);
            getAllKeysFromMap(compile, tokenList);
            for (int i = 0; i < tokenList.size(); i++) {
                buffer.append(tokenList.get((int) pos) + " = " + compile.get(tokenList.get((int) pos)) + ";\n");
                if (isNextToken) {
                    pos++;
                }
            }
            return buffer.toString();
        } catch (Exception e) {
            throw new TokenizeExceptions(String.valueOf(e));
        }
    }

    private static void getAllKeysFromMap(Map<String, Object> _map, ArrayList<String> _output) {
        if (_output == null) return;
        _output.clear();
        if (_map == null || _map.size() < 1) return;
        for (Map.Entry<String, Object> _entry : _map.entrySet()) {
            _output.add(_entry.getKey());
        }
    }
}
