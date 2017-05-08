package com.github.rskupnik.output;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MetaMap extends MetaObject {

    private final Map<String, Object> data = new HashMap<>();

    MetaMap(String id, Map<String, Object> data) {
        super(id);
        this.data.putAll(data);
    }

    public final Map<String, Object> getData() {
        return Collections.unmodifiableMap(data);
    }

    public final Object getObject(String key) {
        return data.get(key);
    }

    public final String getString(String key) {
        return (String) data.get(key);
    }

    public final int getInt(String key) {
        return (int) data.get(key);
    }

    public final boolean getBool(String key) {
        return (boolean) data.get(key);
    }
}
