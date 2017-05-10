package com.github.rskupnik.internal.processors;

import com.github.rskupnik.exceptions.WordplaySyntaxException;
import com.github.rskupnik.output.MetaList;
import com.github.rskupnik.output.MetaMap;
import com.github.rskupnik.output.MetaObject;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.List;

public final class CodeProcessor {

    private EmissionProcessor emissionProcessor = new EmissionProcessor();

    public Triplet<String, ArrayList<Pair<String, String>>, ArrayList<MetaObject>> parse(String input) throws WordplaySyntaxException {
        int delimIndex = input.indexOf("\n$\n");
        if (delimIndex == -1)
            return Triplet.with(input, null, null);

        String data = input.substring(0, delimIndex);
        Triplet<String, ArrayList<Pair<String, String>>, ArrayList<MetaObject>> output =
                Triplet.with(data,
                        new ArrayList<Pair<String, String>>(),
                        new ArrayList<MetaObject>()
                );

        String code = input.substring(delimIndex+3);
        String[] codeLines = code.split("\n");
        for (String codeLine : codeLines) {
            parseLine(codeLine, output);
        }

        return output;
    }

    private void parseLine(String input, Triplet<String, ArrayList<Pair<String, String>>, ArrayList<MetaObject>> output) throws WordplaySyntaxException {
        if (input == null || input.equals(""))
            return;

        String token = input.substring(0, 1);
        if (token == null || token.equals("") || !(token.startsWith("<") || token.startsWith(">")))
            throw new WordplaySyntaxException();

        String rest = input.substring(1).trim();
        switch (token) {
            case ">":   // Injection
                String name = rest.substring(0, rest.indexOf(" "));
                String body = rest.substring(rest.indexOf(" ")+1);
                output.getValue1().add(Pair.with(name, body));
                break;
            case "<":   // Emission
                output.getValue2().add(emissionProcessor.processMeta(rest));
                break;
        }
    }
}
