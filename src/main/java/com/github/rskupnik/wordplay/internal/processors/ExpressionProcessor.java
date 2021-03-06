/*
 Wordplay - text processing engine for dynamic storytelling
 Copyright (C) 2017  Radoslaw Skupnik

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

    private static final Pattern PATTERN_TERNARY = Pattern.compile("\\{\\s*(\\w+)\\s*\\?\\s*(.+)\\s*\\|\\s*(.+)\\s*\\}");
    private static final Pattern PATTERN_MATCHING = Pattern.compile("\\{\\s*((\\w+):(\\w+))\\s+([^\\{\\}\\\\|]+)((\\s*\\|:\\w+\\s*[^\\\\|]+\\s*)*)(\\s*\\|\\s*([^\\\\|]+?)\\})");
    private static final Pattern PATTERN_SUBGROUP = Pattern.compile("\\|:(\\w+)\\s*([^\\\\|]+)\\s*(\\?\\=\\|)?");

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

            // Assume false as default value of this variable if it's missing
            chosenBool = chosenBool != null ? chosenBool : false;

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
        outer: while (matcher.find()) {
            String varName = matcher.group(2);
            String varValue = matcher.group(3);
            String payloadTrue = matcher.group(4);
            String payloadFalse = matcher.group(5);
            String payloadFallback = matcher.group(8);

            String expectedValue = variables.get(varName);
            if (expectedValue == null || expectedValue.length() == 0) {
                accept(payloadFallback, matcher, sb);
                continue;
            }

            if (expectedValue.equals(varValue)) {
                accept(payloadTrue, matcher, sb);
            } else {
                if (payloadFalse != null && payloadFalse.length() != 0) {
                    List<Pair<String, String>> matchPairs = extractMatchPairs(payloadFalse);
                    for (Pair<String, String> matchPair : matchPairs) {
                        if (expectedValue.equals(matchPair.getValue0())) {
                            String val = matchPair.getValue1();
                            accept(val, matcher, sb);
                            continue outer;
                        }
                    }
                    accept(payloadFallback, matcher, sb);
                } else {
                    accept(payloadFallback, matcher, sb);
                }
            }

            /*String expectedValue = variables.get(varName);
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

            expressionsProcessedNumber++;*/
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    private void accept(String chosen, Matcher matcher, StringBuffer sb) {
        if (chosen.endsWith(" "))
            chosen = chosen.substring(0, chosen.length() - 1);

        matcher.appendReplacement(sb, chosen);

        expressionsProcessedNumber++;
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
