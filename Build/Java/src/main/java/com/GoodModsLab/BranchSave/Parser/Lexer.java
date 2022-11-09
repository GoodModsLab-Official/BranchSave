package com.GoodModsLab.BranchSave.Parser;

import com.GoodModsLab.BranchSave.Exceptions.TokenizeExceptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public final class Lexer {
    private double pos;
    private double pos1;

    private String code;
    private boolean isNextToken;
    private final String context;

    private final ArrayList<String> tokenList = new ArrayList<>();
    public  final HashMap jsonCompile = new HashMap();
    private HashMap compile = new HashMap<>();

    public Lexer(String context) {
        this.context = context;
    }

    public void tokenize() throws TokenizeExceptions {
        pos = 0;
        code = context.trim();
        isNextToken = true;
        try {
            tokenList.addAll(Arrays.asList(code.split(";")));
            for (int i = 0; i < tokenList.size(); i++) {
                compile = new Parser(tokenList.get((int) pos), jsonCompile).parser();
                if(isNextToken) {
                    pos++;
                }
            }
            new Compile(compile).toJson();
        } catch (Exception e) {
            throw new TokenizeExceptions(String.valueOf(e));
        }

    }
}
