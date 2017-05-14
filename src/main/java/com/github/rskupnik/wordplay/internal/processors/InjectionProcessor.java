package com.github.rskupnik.wordplay.internal.processors;

import com.github.rskupnik.wordplay.exceptions.WordplayInjectionException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class InjectionProcessor {

    private static final Pattern PATTERN = Pattern.compile("\\{>\\s?(\\w+)\\s?}");

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
