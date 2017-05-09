package com.github.rskupnik;

import com.github.rskupnik.exceptions.WordplayException;
import com.github.rskupnik.internal.expressions.ExpressionFinder;
import com.github.rskupnik.internal.expressions.InjectionExpression;
import com.github.rskupnik.internal.injection.Injector;
import com.github.rskupnik.output.AnchoredObject;
import com.github.rskupnik.output.MetaObject;
import com.github.rskupnik.output.WordplayOutput;

import java.util.*;

public class WordplayImpl implements Wordplay {

    private ExpressionFinder expressionFinder = new ExpressionFinder();
    private Injector injector = new Injector();

    private Map<String, Boolean> booleanVariablesMap = new HashMap<>();
    private Map<String, String> injectedObjects = new HashMap<>();

    @Override
    public WordplayOutput process(String input) throws WordplayException {
        String processedInjection = input;
        do {
            processedInjection = injector.inject(processedInjection, injectedObjects);
        } while (injector.getExpressionsProcessedNumber() != 0);
        return new WordplayOutput(processedInjection, new ArrayList<AnchoredObject>(), new ArrayList<MetaObject>());
    }

    @Override
    public void reset() {
        booleanVariablesMap.clear();
    }

    @Override
    public void setVariable(String var, boolean value) {
        booleanVariablesMap.put(var, value);
    }

    @Override
    public void setVariable(String var, String value) {

    }

    @Override
    public void inject(String id, String value) {
        injectedObjects.put(id, value);
    }

    public static void main(String[] args) throws Exception {
        Wordplay wordplay = new WordplayImpl();
        wordplay.inject("yo", "motherfucker");
        wordplay.process("Yo, {> yo } {>yo } {> yo} {>yo} {> yo}!");
    }
}
