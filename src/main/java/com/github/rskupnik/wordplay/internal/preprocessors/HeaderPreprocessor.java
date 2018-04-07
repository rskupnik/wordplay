package com.github.rskupnik.wordplay.internal.preprocessors;

import java.util.HashMap;
import java.util.Map;

public class HeaderPreprocessor {

    private static final String DELIMINATOR = "\n!$\n";

    public Map<String, String> process(String input) {
        final Map<String, String> output = new HashMap<>();

        final int deliminatorIndex = input.indexOf(DELIMINATOR);
        if (deliminatorIndex == -1)
            return output;

        final String body = input.substring(deliminatorIndex + DELIMINATOR.length());
        for (String line : body.split("\n")) {
            final String[] split = line.split(" ");
            if (split == null || split.length <= 1)
                continue;

            output.put(split[0], intoValue(split));
        }

        return output;
    }

    private String intoValue(String[] input) {
        final StringBuilder output = new StringBuilder();
        for (int i = 1; i < input.length; i++) {
            output.append(input[i]);
        }
        return output.toString();
    }
}
