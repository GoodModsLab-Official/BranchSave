package com.GoodModsLab.BranchSave.Parser;

import com.GoodModsLab.BranchSave.Exceptions.ParserExceptions;

import java.util.List;

public interface Function {
    double apply(List<Double> args) throws ParserExceptions;
}
