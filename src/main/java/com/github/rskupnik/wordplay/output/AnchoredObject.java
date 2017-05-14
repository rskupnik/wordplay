package com.github.rskupnik.wordplay.output;

import java.util.HashMap;
import java.util.Map;

public final class AnchoredObject {

    private final String text;
    private final int position;
    private final Map<String, String> parameters = new HashMap<>();

    public AnchoredObject(String text, int position) {
        this.text = text;
        this.position = position;
    }

    public AnchoredObject(String text, int position, Map<String, String> parameters) {
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
        return Boolean.parseBoolean(parameters.get(key));
    }

    public String getStringParam(String key) {
        return (String) parameters.get(key);
    }

    public Integer getIntParam(String key) {
        Integer out = null;
        try {
            out = Integer.parseInt(parameters.get(key));
        } finally {
            return out;
        }
    }
}
