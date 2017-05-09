package com.github.rskupnik;

import com.github.rskupnik.exceptions.WordplayException;
import com.github.rskupnik.output.WordplayOutput;

public interface Wordplay {
    WordplayOutput process(String input) throws WordplayException;
    void reset();
    void setVariable(String var, boolean value);
    void setVariable(String var, String value);
    void inject(String id, String value);
}
