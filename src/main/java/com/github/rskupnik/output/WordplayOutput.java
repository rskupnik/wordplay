package com.github.rskupnik.output;

import com.github.rskupnik.output.AnchoredObject;

import java.util.*;

public final class WordplayOutput {

    private final String text;
    private final List<AnchoredObject> anchoredObjects = new ArrayList<>();
    private final List<MetaObject> metaObjects = new ArrayList<>();

    public WordplayOutput(String text) {
        this.text = text;
    }

    public WordplayOutput(String text, List<AnchoredObject> anchoredObjects) {
        this(text);
        this.anchoredObjects.addAll(anchoredObjects);
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