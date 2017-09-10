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

import com.github.rskupnik.wordplay.exceptions.WordplayInjectionException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class InjectionProcessor {

    private static final Pattern PATTERN = Pattern.compile("\\{>\\s?(\\w+)\\s?\\}");

    private int expressionsProcessedNumber;

    /**
     * Finds patterns matching the injection expression and splits them
     * into a key-value pair and replaces them in the text.
     * @throws WordplayInjectionException if a key is not found
     * @return the input text with all injections replaced by their values
     */
    public String inject(String text, Map<String, String> injectedObjects) throws WordplayInjectionException {
        expressionsProcessedNumber = 0;

        StringBuffer sb = new StringBuffer();
        Matcher matcher = PATTERN.matcher(text);
        while (matcher.find()) {
            String varName = matcher.group(1);
            String payload = injectedObjects.get(varName);
            if (payload == null || payload.equals(""))
                throw new WordplayInjectionException();
            matcher.appendReplacement(sb, payload);
            expressionsProcessedNumber++;
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    public int getExpressionsProcessedNumber() {
        return expressionsProcessedNumber;
    }
}
