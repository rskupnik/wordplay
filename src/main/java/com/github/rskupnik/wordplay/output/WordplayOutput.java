package com.github.rskupnik.wordplay.output;

import java.util.*;

public final class WordplayOutput {

    private final String text;
    private final List<AnchoredObject> anchoredObjects = new ArrayList<>();
    private final List<MetaObject> metaObjects = new ArrayList<>();

    public WordplayOutput(String text) {
        this.text = text;
    }

    public WordplayOutput(String text, List<AnchoredObject> anchoredObjects, List<MetaObject> metaObjects) {
        this(text);
        if (anchoredObjects != null)
            this.anchoredObjects.addAll(anchoredObjects);
        if (metaObjects != null)
            this.metaObjects.addAll(metaObjects);
    }

    public final String getText() {
        return text;
    }

    public final List<AnchoredObject> getAnchoredObjects() {
        return Collections.unmodifiableList(anchoredObjects);
    }

    public final List<MetaObject> getMetaObjects() {
        return Collections.unmodifiableList(metaObjects);
    }
}
