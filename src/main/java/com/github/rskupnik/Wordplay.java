package com.github.rskupnik;

public interface Wordplay {
    String process(String input);
    void reset();
    void setVariable(String var, boolean value);
    void setVariable(String var, String value);
    void inject(String id, String value);
}
