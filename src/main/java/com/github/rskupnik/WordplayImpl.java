package com.github.rskupnik;

import com.github.rskupnik.output.WordplayOutput;

import java.util.HashMap;
import java.util.Map;

public class WordplayImpl implements Wordplay {

    private Map<String, Boolean> booleanVariablesMap = new HashMap<>();

    @Override
    public WordplayOutput process(String input) {
        int openingBracketIndex = input.indexOf('{');
        int closingBracketIndex = input.indexOf('}');

        if (openingBracketIndex == -1 || closingBracketIndex == -1)
            return null;

        String expression = input.substring(openingBracketIndex+1, closingBracketIndex);

        if (!expression.contains("?") && !expression.contains("|"))
            return null;

        String variable = expression.split("\\?")[0].trim();
        String rest = expression.split("\\?")[1].substring(1);
        String[] split = rest.split("\\s\\|\\s");
        String first = split[0];
        String second = split[1];

        String result = booleanVariablesMap.get(variable) != null &&
                booleanVariablesMap.get(variable) == true ? first : second;

        return new WordplayOutput(
                input.substring(0, openingBracketIndex)
                + result
                + input.substring(closingBracketIndex+1)
        );
    }

    @Override
    public void reset() {
        booleanVariablesMap.clear();
    }

    @Override
    public void setVariable(String var, boolean value) {
        booleanVariablesMap.put(var, value);
    }

    @Override
    public void setVariable(String var, String value) {

    }

    @Override
    public void inject(String id, String value) {

    }
}
