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
import com.github.rskupnik.wordplay.output.MetaObject;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.ArrayList;

public final class CodeProcessor {

    private EmissionProcessor emissionProcessor = new EmissionProcessor();

    /**
     * Parses the code section of the script if it exists.
     * The code section is delimited by "\n$\n" <- a new line with just the $ symbol.
     * The code section is structured line-by-line and so it is parsed.
     * The expected syntax is known so a WordplaySyntaxException can be thrown.
     * @return a Triplet of: Output string with just the data section,
     * a list of key-value pairs which represent injected objects,
     * a list of MetaObjects which represent the emitted objects
     */
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
            default:
                throw new WordplaySyntaxException();
        }
    }
}
