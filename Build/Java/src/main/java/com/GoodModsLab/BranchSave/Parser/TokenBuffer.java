package com.GoodModsLab.BranchSave.Parser;

import java.util.List;

public final class TokenBuffer {

    private static int pos;
    public static List<Token> tokens;

    public TokenBuffer(List<Token> tokens) {
        this.tokens = tokens;
    }

    public static Token next() {
        return tokens.get(pos++);
    }

    public static Token peek() {
        return  tokens.get(0);
    }

    public void back() {
        pos--;
    }

    public  int getPos() {
        return pos;
    }
}
