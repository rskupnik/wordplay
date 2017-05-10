package com.github.rskupnik.internal.processors;

import com.github.rskupnik.exceptions.WordplaySyntaxException;
import com.github.rskupnik.output.MetaList;
import com.github.rskupnik.output.MetaMap;
import com.github.rskupnik.output.MetaObject;

import java.util.*;

public final class EmissionProcessor {

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
}
