package com.github.rskupnik.wordplay.internal.processors;

import com.github.rskupnik.wordplay.exceptions.WordplayProcessingException;
import com.github.rskupnik.wordplay.exceptions.WordplaySyntaxException;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ExpressionProcessor {

    private static final Pattern PATTERN_TERNARY = Pattern.compile("\\{\\s*(\\w+)\\s*\\?\\s*(.+)\\s*\\|\\s*(.+)\\s*}");
    private static final Pattern PATTERN_MATCHING = Pattern.compile("\\{\\s*((\\w+):(\\w+))\\s+([^{}\\\\|]+)((\\s*\\|:\\w+\\s*[^\\\\|]+\\s*)*)(\\s*\\|\\s*([^\\\\|]+?)})");
    private static final Pattern PATTERN_SUBGROUP = Pattern.compile("\\|:(\\w+)\\s*([^\\\\|]+)\\s*(?=\\|)?");

    private int expressionsProcessedNumber;

    /**
     * Both Ternary and Matching expressions are processed together,
     * because we want them to be able to nest each other.
     * @return a String with all expressions processed and replaced
     */
    public String process(String input, Map<String, Boolean> booleanVariables, Map<String, String> variables) throws WordplayProcessingException, WordplaySyntaxException {
        expressionsProcessedNumber = 0;
        String output = processTernary(input, booleanVariables);
        output = processMatching(output, variables);
        return output;
    }

    private String processTernary(String input, Map<String, Boolean> booleanVariables) throws WordplayProcessingException, WordplaySyntaxException {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = PATTERN_TERNARY.matcher(input);
        while (matcher.find()) {
            String varName = matcher.group(1);
            String valTrue = matcher.group(2);
            String valFalse = matcher.group(3);

            Boolean chosenBool = booleanVariables.get(varName);
            if (chosenBool == null)
                throw new WordplayProcessingException();
            String chosen = chosenBool ? valTrue : valFalse;
            if (chosen.endsWith(" "))
                chosen = chosen.substring(0, chosen.length() - 1);
            matcher.appendReplacement(sb, chosen);
            expressionsProcessedNumber++;
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    private String processMatching(String input, Map<String, String> variables) throws WordplayProcessingException, WordplaySyntaxException {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = PATTERN_MATCHING.matcher(input);
        while (matcher.find()) {
            String varName = matcher.group(2);
            String varValue = matcher.group(3);
            String payloadTrue = matcher.group(4);
            String payloadFalse = matcher.group(5);
            String payloadFallback = matcher.group(8);

            String expectedValue = variables.get(varName);
            if (expectedValue == null || expectedValue.equals(" "))
                throw new WordplayProcessingException();

            String chosen = expectedValue.equals(varValue) ? payloadTrue : payloadFallback;

            if (payloadFalse != null && !payloadFalse.equals(" ")) { // There are more options to consider
                List<Pair<String, String>> matchPairs = extractMatchPairs(payloadFalse);
                for (Pair<String, String> matchPair : matchPairs) {
                    if (expectedValue.equals(matchPair.getValue0())) {
                        chosen = matchPair.getValue1();
                        break;
                    }
                }
            }

            if (chosen.endsWith(" "))
                chosen = chosen.substring(0, chosen.length() - 1);

            matcher.appendReplacement(sb, chosen);

            expressionsProcessedNumber++;
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    private List<Pair<String, String>> extractMatchPairs(String input) {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = PATTERN_SUBGROUP.matcher(input);
        List<Pair<String, String>> output = new ArrayList<>();
        while (matcher.find()) {
            String varValue = matcher.group(1);
            String payload = matcher.group(2);
            output.add(new Pair<String, String>(varValue, payload));
        }
        return output;
    }

    public int getExpressionsProcessedNumber() {
        return expressionsProcessedNumber;
    }
}