package com.github.rskupnik.internal.expressions;

public final class InjectionExpression extends Expression {

    private String variableName;

    public InjectionExpression(String text, int startIndex, int endIndex, String variableName) {
        super(text, startIndex, endIndex);
        this.variableName = variableName;
    }

    public String getVariableName() {
        return variableName;
    }
}
