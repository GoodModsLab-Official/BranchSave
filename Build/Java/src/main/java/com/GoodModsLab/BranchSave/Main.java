package com.GoodModsLab.BranchSave;

import com.GoodModsLab.BranchSave.Exceptions.TokenizeExceptions;
import com.GoodModsLab.BranchSave.Parser.Compile;
import com.GoodModsLab.BranchSave.Parser.Lexer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public final class Main {

    private static final int VERSION_MAJOR = 1;
    private static final int VERSION_MINOR = 0;
    private static final int VERSION_PATCH = 6;
    private static final String VERSION_NAME = "Bedrock";
    private static final String VERSION = VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_PATCH + " " + VERSION_NAME;

    public static void main(String[] args) throws IOException, TokenizeExceptions {
       System.out.println("Branch Save - A structured compiled programming language. Designed for Java JVM. It has dynamic typing. \n" +
                "Official website: https://goodmodslab-official.github.io\n" +
                "[Branch Save | v." + VERSION + "] Enter file path for open your file or enter 'brch -help' for more info.");


        try {
            Scanner console = new Scanner(System.in);
            while (true) {
                String consoleVal = console.nextLine();
                String[] Source = consoleVal.split(" ");
                if (Source[0].equals("brch")) {
                    if (Source[1].equals("-ping") || Source[1].equals("-p")) new Console().message("Pong! Successfully!");
                    if (Source[1].equals("-help") || Source[1].equals("-h")) new Console().message(help());
                    if (Source[1].equals("-input") || Source[1].equals("-i")) {
                        StringBuilder buffer = new StringBuilder();
                        for (int i = 2; i < Source.length; i++) {
                            buffer.append(Source[i]);
                        }
                        String program = new Lexer(buffer.toString()).tokenize();
                        new Console().message(program);
                    }
                    if (Source[1].equals("-file") || Source[1].equals("-f")) {
                        if (new SourceLoader().getFileExtension(new File(Source[2])).equals("brch")) {
                            String program = new Lexer(new String(Files.readAllBytes(Paths.get(Source[2])), "UTF-8")).tokenize();
                            new Console().message(program);
                        } else if (new SourceLoader().getFileExtension(new File(Source[2])).equals("json")) {
                            String program = new Lexer(new String(Files.readAllBytes(Paths.get(Source[2])), "UTF-8")).tokenizeJson();
                            new Console().message(program);
                        } else {
                            new Console().err("Unsupported file type!");
                        }

                    }
                    if (Source[1].equals("-exit") || Source[1].equals("-e")) break;
                }
            }
        } catch (Exception e) {
            new Console().err(String.valueOf(e));
        }

    }

    private static String help() {
        return "[Branch Save | v." + VERSION + "]\n" +
                "Commands 'brch -help || -h' - Get more information." +
                "\n'brch -ping || -p' - Send a request to the machine to find out its return." +
                "\n'brch -input || -i (String code)' - Run the lexer on the issued code." +
                "\n'brch -file || -file (String path)' - Run the lexer on the issued file." +
                "\n'brch -exit || -e' - Close language usage.";
    }

}