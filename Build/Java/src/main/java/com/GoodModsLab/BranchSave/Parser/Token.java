package com.GoodModsLab.BranchSave.Parser;

public final class Token {
    TokenType type;
    String context;

    public Token(TokenType type, String context) {
        this.type = type;
        this.context = context;
    }

    public Token(TokenType type, Character context) {
        this.type = type;
        this.context = context.toString();
    }

    @Override
    public String toString() {
        return "Token: " + type +
                "\nValue: " + context;
    }
}
