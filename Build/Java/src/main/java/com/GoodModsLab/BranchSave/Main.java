package com.GoodModsLab.BranchSave;

import com.GoodModsLab.BranchSave.Exceptions.TokenizeExceptions;
import com.GoodModsLab.BranchSave.Parser.Lexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public final class Main {

    private static final int VERSION_MAJOR = 1;
    private static final int VERSION_MINOR = 0;
    private static final int VERSION_PATCH = 0;
    private static final String VERSION = VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_PATCH;

    public static void main(String[] args) throws IOException, TokenizeExceptions {
        // C:\Users\vladd\IdeaProjects\BranchSave\src\main\java\com\GoodModsLab\BranchSave\Hello.brch
        System.out.println("Branch Save - A structured compiled programming language. Designed for Java JVM. It has dynamic typing. \n" +
                "Official website: https://goodmodslab-official.github.io\n" +
                "[Branch Save | v." + VERSION + "] Enter file path for open your file or enter 'brch -help' for compile your code.");

        Scanner sc = new Scanner(System.in);
        String console = sc.next();
        String input = new String(Files.readAllBytes(Paths.get(console)), "UTF-8");
        new Lexer(input).tokenize();

    }
}