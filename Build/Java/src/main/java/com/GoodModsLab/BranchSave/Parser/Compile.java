package com.GoodModsLab.BranchSave.Parser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class Compile {

    private HashMap jsonCompile;

    public  Compile() {}
    public Compile(HashMap jsonCompile) {
        this.jsonCompile = jsonCompile;
    }

    public String toJson() {
        return new Gson().toJson(jsonCompile);
    }

    public HashMap fromJson(String context) {
        return jsonCompile = new Gson().fromJson(context, new TypeToken<HashMap<String, Object>>(){}.getType());
    }
}
