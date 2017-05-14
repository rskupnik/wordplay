package com.github.rskupnik.wordplay.output;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MetaMap extends MetaObject {

    private final Map<String, String> data = new HashMap<>();

    public MetaMap(String id, Map<String, String> data) {
        super(id);
        this.data.putAll(data);
    }

    public final Map<String, String> getData() {
        return Collections.unmodifiableMap(data);
    }

    public final Object getObject(String key) {
        return data.get(key);
    }

    public final String getString(String key) {
        return data.get(key);
    }

    public final Integer getInt(String key) {
        Integer out = null;
        try {
            out = Integer.parseInt(data.get(key));
        } finally {
            return out;
        }
    }

    public final boolean getBool(String key) {
        return Boolean.parseBoolean(data.get(key));
    }
}
