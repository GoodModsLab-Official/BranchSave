package com.GoodModsLab.BranchSave.Parser;

import com.google.gson.Gson;

import java.util.HashMap;

public final class Compile {

    private HashMap jsonCompile;

    public Compile(HashMap jsonCompile) {
        this.jsonCompile = jsonCompile;
    }

    public void toJson() {
        System.out.println(new Gson().toJson(jsonCompile));
    }
}
