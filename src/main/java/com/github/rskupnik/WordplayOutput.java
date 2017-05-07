package com.github.rskupnik;

import com.github.rskupnik.objects.AnchoredObject;

import java.util.*;

public final class WordplayOutput {

    private String text;
    private List<AnchoredObject> anchoredObjects = new ArrayList<>();

    WordplayOutput(String text) {
        this.text = text;
    }

    WordplayOutput(String text, List<AnchoredObject> anchoredObjects) {
        this(text);
        this.anchoredObjects = anchoredObjects;
    }

    public String getText() {
        return text;
    }

    public List<AnchoredObject> getAnchoredObjects() {
        return Collections.unmodifiableList(anchoredObjects);
    }
}
