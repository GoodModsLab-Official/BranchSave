package com.GoodModsLab.BranchSave;

import static java.lang.System.*;

public final class Console {
    private String PREFIX = "Branch.panel: ";

    public Console() {}

    public void message(String message) {
        out.println(PREFIX + message);
    }
    public void message(String message, int type) {
        if (type == 0) {
            out.println(PREFIX + message);
        }
        if (type == 1) {
            out.println(message);
        }
        if (type == 2) {
            out.println("\n");
        }
    }

    public void err(String message) {
        out.println(">> Error: " + PREFIX + message);
    }
}
