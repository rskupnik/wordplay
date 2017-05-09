package com.github.rskupnik.internal.expressions;

public abstract class Expression {

    private String text;
    private int startIndex;
    private int endIndex;

    public Expression(String text, int startIndex, int endIndex) {
        this.text = text;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public String getText() {
        return text;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
}
