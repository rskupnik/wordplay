package com.github.rskupnik.internal.processors;

import com.github.rskupnik.exceptions.WordplayProcessingException;
import com.github.rskupnik.exceptions.WordplaySyntaxException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TernaryProcessor {

    private static final Pattern PATTERN_SHORT = Pattern.compile("\\{\\s*(\\w+)\\s*\\?\\s*(.+)\\s*\\|\\s*(.+)\\s*}");
    private static final Pattern PATTERN_FULL = Pattern.compile("\\{\\s*((\\w+):(\\w+))\\s*\\?\\s*([^\\\\|]+)(\\s*\\|:(\\w+)\\s*([^\\\\|]+)\\s*)+(\\s*\\|\\s*([^\\\\|]+)\\s*)}");
    private static final Pattern PATTERN_SUBGROUP = Pattern.compile("\\|:(\\w+)\\s*([^\\\\|]+)\\s*[\\\\|$]");
    private static final Pattern PATTERN_REMAINING = Pattern.compile("\\{.*}");

    private int expressionsProcessedNumber;

    public String process(String input, Map<String, Boolean> booleanVariables, Map<String, String> variables) throws WordplayProcessingException, WordplaySyntaxException {
        expressionsProcessedNumber = 0;
        String output = processShorthand(input, booleanVariables, variables);
        return output;
    }

    private String processShorthand(String input, Map<String, Boolean> booleanVariables, Map<String, String> variables) throws WordplayProcessingException {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = PATTERN_SHORT.matcher(input);
        while (matcher.find()) {
            String varName = matcher.group(1);
            String valTrue = matcher.group(2);
            String valFalse = matcher.group(3);
            Boolean chosenBool = booleanVariables.get(varName);
            if (chosenBool == null)
                throw new WordplayProcessingException();
            String chosen = chosenBool ? valTrue : valFalse;
            if (chosen.endsWith(" "))
                chosen = chosen.substring(0, chosen.length()-1);
            matcher.appendReplacement(sb, chosen);
            expressionsProcessedNumber++;
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    public void checkInvalidPatternsRemaining(String input) throws WordplaySyntaxException {
        Matcher matcher = PATTERN_REMAINING.matcher(input);
        if (matcher.find()) {
            throw new WordplaySyntaxException();
        }
    }

    public int getExpressionsProcessedNumber() {
        return expressionsProcessedNumber;
    }
}
