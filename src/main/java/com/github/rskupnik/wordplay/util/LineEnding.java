package com.github.rskupnik.wordplay.util;

public class LineEnding {

    public static String deduce(String input) {
        return input.contains("\r\n") ? "\r\n" : (input.contains("\n") ? "\n" : "\r");
    }
}
