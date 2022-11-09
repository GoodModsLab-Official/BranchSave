package com.GoodModsLab.BranchSave.Exceptions;

public final class ParserExceptions extends Exception {
    public ParserExceptions(String message, String line) {
        super(message + line);
    }
}
