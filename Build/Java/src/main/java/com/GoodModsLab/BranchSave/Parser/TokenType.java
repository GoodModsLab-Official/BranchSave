package com.GoodModsLab.BranchSave.Parser;

public enum TokenType {
    NUMBER,
    WORD,
    VARIABLE,

    PLUS, // +
    MINUS, // -
    MUL, // *
    DIV, // /

    L_PARENT, // (
    R_PARENT, // )
    COMMA, // ,

    EOF
}
