package com.GoodModsLab.BranchSave.Parser.AST;

import com.GoodModsLab.BranchSave.Libs.Variables;

import java.util.HashMap;

public final class VariablesExpression implements Expression {

    private final String name;
    private final HashMap jsonCompile;

    public VariablesExpression(String name, HashMap jsonCompile) {
        this.jsonCompile = jsonCompile;
        this.name = name;
    }

    @Override
    public double eval() {
        if (!Variables.isExists(name)) {
            if (!jsonCompile.containsKey(name)) {
                throw new RuntimeException("Constant does not exists");
            } else {
                double value = Double.valueOf((String) jsonCompile.get(name));
                return value;
            }
        } else {
            return Variables.get(name);
        }
    }

    @Override
    public String toString() {
        return String.format("%s", name);
    }
}
