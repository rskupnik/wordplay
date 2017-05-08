package com.github.rskupnik.output;

import java.util.HashMap;
import java.util.Map;

public final class AnchoredObject {

    private final String text;
    private final int position;
    private final Map<String, Object> parameters = new HashMap<>();

    AnchoredObject(String text, int position) {
        this.text = text;
        this.position = position;
    }

    AnchoredObject(String text, int position, Map<String, Object> parameters) {
        this(text, position);
        this.parameters.putAll(parameters);
    }

    public String getText() {
        return text;
    }

    public int getPosition() {
        return position;
    }

    public Object getParam(String key) {
        return parameters.get(key);
    }

    public boolean getBoolParam(String key) {
        return (boolean) parameters.get(key);
    }

    public String getStringParam(String key) {
        return (String) parameters.get(key);
    }

    public int getIntParam(String key) {
        return (int) parameters.get(key);
    }
}