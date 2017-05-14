package com.github.rskupnik.wordplay.output;

import java.util.Collection;

public abstract class MetaObject {

    private final String id;

    protected MetaObject(String id) {
        this.id = id;
    }

    public final String getId() {
        return id;
    }
}
