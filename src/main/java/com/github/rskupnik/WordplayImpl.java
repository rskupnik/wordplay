package com.github.rskupnik;

import com.github.rskupnik.exceptions.WordplayException;
import com.github.rskupnik.internal.expressions.ExpressionFinder;
import com.github.rskupnik.internal.processors.CodeProcessor;
import com.github.rskupnik.internal.processors.InjectionProcessor;
import com.github.rskupnik.output.AnchoredObject;
import com.github.rskupnik.output.MetaObject;
import com.github.rskupnik.output.WordplayOutput;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.*;

public class WordplayImpl implements Wordplay {

    private ExpressionFinder expressionFinder = new ExpressionFinder();
    private InjectionProcessor injectionProcessor = new InjectionProcessor();
    private CodeProcessor codeProcessor = new CodeProcessor();

    private Map<String, Boolean> booleanVariablesMap = new HashMap<>();
    private Map<String, String> injectedObjects = new HashMap<>();

    @Override
    public WordplayOutput process(String input) throws WordplayException {
        //region Process Code Section
        Triplet<String, ArrayList<Pair<String, String>>, ArrayList<MetaObject>> codeProcessingOutput =
                codeProcessor.parse(input);

        // Get the internal injections that were processed and put them in the same map as external ones
        if (codeProcessingOutput.getValue1() != null) {
            for (Pair<String, String> injectionPair : codeProcessingOutput.getValue1()) {
                injectedObjects.put(injectionPair.getValue0(), injectionPair.getValue1());
            }
        }

        input = codeProcessingOutput.getValue0();   // Drop the code part, leave only data
        //endregion

        //region Injection
        // Perform injection until no injection expressions are found
        String processedInjection = input;
        do {
            processedInjection = injectionProcessor.inject(processedInjection, injectedObjects);
        } while (injectionProcessor.getExpressionsProcessedNumber() != 0);
        //endregion

        //region Processing
        // Process ternary expressions
        //endregion

        //region Emission

        //endregion

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
        WordplayOutput output = wordplay.process("Yo, {> yo } {>yo } {> yo} {>yo} {> yo}!\n$\n<l 0 hiho|hehe|haha|lol\n> 0 x");
        System.out.println(output.getText());
    }
}
