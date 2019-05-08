package com.github.rskupnik.wordplay.internal.preprocessors;

import com.github.rskupnik.wordplay.util.LineEnding;

import java.util.HashMap;
import java.util.Map;

public class HeaderPreprocessor {

    public static final String DELIMINATOR = "!$";

    public Map<String, String> process(String input) {
        final Map<String, String> output = new HashMap<>();
        final String lineEnding = LineEnding.deduce(input);

        final int deliminatorIndex = input.indexOf(lineEnding + DELIMINATOR + lineEnding);
        if (deliminatorIndex == -1)
            return output;

        final String body = input.substring(deliminatorIndex + DELIMINATOR.length() + (lineEnding.length() * 2));
        for (String line : body.split(lineEnding)) {
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
