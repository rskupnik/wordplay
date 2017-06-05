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

import com.github.rskupnik.wordplay.exceptions.WordplaySyntaxException;
import com.github.rskupnik.wordplay.output.AnchoredObject;
import com.github.rskupnik.wordplay.output.MetaList;
import com.github.rskupnik.wordplay.output.MetaMap;
import com.github.rskupnik.wordplay.output.MetaObject;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class EmissionProcessor {

    private static final Pattern PATTERN_ANCHORED = Pattern.compile("\\{<\\s*([\\w:\\|]+)\\s([\\w\\s]+)\\s*}");
    private static final Pattern PATTERN_SUBGROUP = Pattern.compile("(\\w+):(\\w+)(?=\\|)?");
    private static final Pattern PATTERN_TOKEN = Pattern.compile("\\$@s\\d+\\s");
    private static final String TOKEN_START = "$@s";

    private int internalIndex = 0;
    private int expressionsProcessedNumber = 0;

    /**
     * The second part of processing emitted objects.
     * Takes the object components returned by processAnchoredObjects
     * and constructs a list of actual AnchoredObject objects.
     * This needs to be a two-step process because the AnchoredObjects
     * have to be found and processed first and then their final positions
     * in the final text have to be determined once the text is ready.
     * @return a Pair with the output String and a list of AnchoredObjects
     */
    public Pair<String, List<AnchoredObject>> constructAnchoredObjects(String input, List<Triplet<String, Integer, Map<String, String>>> objectComponents) {
        List<AnchoredObject> output = new ArrayList<>();

        // Construct anchored objects from components by tracking the temporary tokens inserted while processing
        int acc = 0;
        for (Triplet<String, Integer, Map<String, String>> component : objectComponents) {
            String token = TOKEN_START+component.getValue1()+" ";
            int position = input.indexOf(token) - acc;
            acc += token.length();
            output.add(new AnchoredObject(component.getValue0(), position, component.getValue2()));
        }

        // Remove the temporary tokens
        Matcher matcher = PATTERN_TOKEN.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "");
        }
        matcher.appendTail(sb);

        return new Pair<>(sb.toString(), output);
    }

    /**
     * The first part of processing emitted objects.
     * Finds the anchored object pattern in text, splits it accordingly
     * and returns all component needed by constructAnchoredObjects
     * to be able to construct the actual objects while preserving their
     * actual position in the final text.
     * @return A Pair with the output string and a list of triplets that
     * contain object components: text, index and a map of key-value pairs
     */
    public Pair<String, List<Triplet<String, Integer, Map<String, String>>>> processAnchoredObjects(String input) {
        expressionsProcessedNumber = 0;
        List<Triplet<String, Integer, Map<String, String>>> objects = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        Matcher matcher = PATTERN_ANCHORED.matcher(input);
        while (matcher.find()) {
            String params = matcher.group(1);
            String value = matcher.group(2);

            Map<String, String> parameters = extractParameters(params);
            int index = nextIndex();

            objects.add(Triplet.with(value, index, parameters));

            String replacement = Matcher.quoteReplacement(TOKEN_START+index+" " + value);
            matcher.appendReplacement(sb, replacement);

            expressionsProcessedNumber++;
        }
        matcher.appendTail(sb);

        return new Pair<>(sb.toString(), objects);
    }

    private Map<String, String> extractParameters(String input) {
        Map<String, String> output = new HashMap<>();
        Matcher matcher = PATTERN_SUBGROUP.matcher(input);
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            output.put(key, value);
        }
        return output;
    }

    private int nextIndex() {
        if (internalIndex > 1000)
            internalIndex = 0;

        return internalIndex++;
    }

    public MetaObject processMeta(String line) throws WordplaySyntaxException {
        if (line.startsWith("<"))
            line = line.substring(1);

        char type = line.charAt(0);
        if (type != 'm' && type != 'l')
            throw new WordplaySyntaxException();

        line = line.substring(1).trim();

        return type == 'm' ? processMetaMap(line) : processMetaList(line);
    }

    private MetaMap processMetaMap(String line) throws WordplaySyntaxException {
        if (line == null || line.equals(""))
            throw new WordplaySyntaxException();

        Map<String, String> outputElementsMap = new HashMap<>();
        String id = line.substring(0, line.indexOf(" "));
        String body = line.substring(line.indexOf(" ")+1);
        String[] tokens = body.split("\\|");
        for (String token : tokens) {
            if (!token.contains(":"))
                throw new WordplaySyntaxException();

            try {
                String[] elements = token.split(":");
                outputElementsMap.put(elements[0], elements[1]);
            } catch (Exception e) {
                throw new WordplaySyntaxException();
            }
        }

        return new MetaMap(id, outputElementsMap);
    }

    private MetaList processMetaList(String line) throws WordplaySyntaxException {
        if (line == null || line.equals(""))
            throw new WordplaySyntaxException();

        String id = line.substring(0, line.indexOf(" "));
        String body = line.substring(line.indexOf(" ")+1);
        String[] tokens = body.split("\\|");

        return new MetaList(id, Arrays.asList(tokens));
    }

    public int getExpressionsProcessedNumber() {
        return expressionsProcessedNumber;
    }
}
