package com.github.rskupnik.output;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MetaList extends MetaObject {

    private final List<String> data = new ArrayList<>();

    public MetaList(String id, List<String> data) {
        super(id);
        this.data.addAll(data);
    }

    public final List<String> getData() {
        return Collections.unmodifiableList(data);
    }

    public final Object getObject(int index) {
        return data.get(index);
    }

    public final String getString(int index) {
        return data.get(index);
    }

    public final int getInt(int index) {
        Integer out = null;
        try {
            out = Integer.parseInt(data.get(index));
        } finally {
            return out;
        }
    }

    public final boolean getBool(int index) {
        return Boolean.parseBoolean(data.get(index));
    }
}
