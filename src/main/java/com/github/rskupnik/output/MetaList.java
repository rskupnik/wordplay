package com.github.rskupnik.output;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MetaList extends MetaObject {

    private final List<Object> data = new ArrayList<>();

    MetaList(String id, List<Object> data) {
        super(id);
        this.data.addAll(data);
    }

    public final List<Object> getData() {
        return Collections.unmodifiableList(data);
    }

    public final Object getObject(int index) {
        return data.get(index);
    }

    public final String getString(int index) {
        return (String) data.get(index);
    }

    public final int getInt(int index) {
        return (int) data.get(index);
    }

    public final boolean getBool(int index) {
        return (boolean) data.get(index);
    }
}
