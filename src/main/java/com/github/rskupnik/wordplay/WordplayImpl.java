package com.github.rskupnik.wordplay;

import com.github.rskupnik.wordplay.exceptions.WordplayException;
import com.github.rskupnik.wordplay.internal.processors.CodeProcessor;
import com.github.rskupnik.wordplay.internal.processors.EmissionProcessor;
import com.github.rskupnik.wordplay.internal.processors.ExpressionProcessor;
import com.github.rskupnik.wordplay.internal.processors.InjectionProcessor;
import com.github.rskupnik.wordplay.output.AnchoredObject;
import com.github.rskupnik.wordplay.output.MetaObject;
import com.github.rskupnik.wordplay.output.WordplayOutput;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.*;

public class WordplayImpl implements Wordplay {

    private InjectionProcessor injectionProcessor = new InjectionProcessor();
    private CodeProcessor codeProcessor = new CodeProcessor();
    private ExpressionProcessor expressionProcessor = new ExpressionProcessor();
    private EmissionProcessor emissionProcessor = new EmissionProcessor();

    private Map<String, Boolean> booleanVariablesMap = new HashMap<>();
    private Map<String, String> variablesMap = new HashMap<>();
    private Map<String, String> injectedObjects = new HashMap<>();

    @Override
    public WordplayOutput process(String input) throws WordplayException {
        //region Process Code Section
        // Stripped text | A list of internal injections in the key-value pair format | A list of emitted objects
        Triplet<String, ArrayList<Pair<String, String>>, ArrayList<MetaObject>> codeProcessingOutput =
                codeProcessor.parse(input);

        // Get the internal injections that were processed and put them in the same map as external ones
        if (codeProcessingOutput.getValue1() != null) {
            for (Pair<String, String> injectionPair : codeProcessingOutput.getValue1()) {
                injectedObjects.put(injectionPair.getValue0(), injectionPair.getValue1());
            }
        }

        String processedCodeOutput = codeProcessingOutput.getValue0();   // Drop the code part, leave only data
        List<MetaObject> metaObjects = codeProcessingOutput.getValue2();
        //endregion

        //region Injection
        // Perform injection until no injection expressions are found
        String processedInjectionOutput = processedCodeOutput;
        do {
            processedInjectionOutput = injectionProcessor.inject(processedInjectionOutput, injectedObjects);
        } while (injectionProcessor.getExpressionsProcessedNumber() != 0);
        //endregion

        //region Processing
        // Perform ternary/matching until no ternary/matching expressions are found
        String processedExpressionOutput = processedInjectionOutput;
        do {
            processedExpressionOutput = expressionProcessor.process(processedExpressionOutput, booleanVariablesMap, variablesMap);
        } while (expressionProcessor.getExpressionsProcessedNumber() != 0);
        //endregion

        //region Emission
        String processedEmission = processedExpressionOutput;
        List<Triplet<String, Integer, Map<String, String>>> anchoredObjectComponents = new ArrayList<>();
        do {
            Pair<String, List<Triplet<String, Integer, Map<String, String>>>> emissionOutput =
                    emissionProcessor.processAnchoredObjects(processedEmission);
            processedEmission = emissionOutput.getValue0();
            anchoredObjectComponents.addAll(emissionOutput.getValue1());
        } while (emissionProcessor.getExpressionsProcessedNumber() != 0);

        Pair<String, List<AnchoredObject>> emissionFinalOutput =
                emissionProcessor.constructAnchoredObjects(processedEmission, anchoredObjectComponents);
        String processedFinalOutput = emissionFinalOutput.getValue0();
        List<AnchoredObject> anchoredObjects = emissionFinalOutput.getValue1();
        //endregion

        return new WordplayOutput(processedFinalOutput, anchoredObjects, metaObjects);
    }

    @Override
    public void reset() {
        booleanVariablesMap.clear();
        variablesMap.clear();
        injectedObjects.clear();
    }

    @Override
    public void setVariable(String var, boolean value) {
        booleanVariablesMap.put(var, value);
    }

    @Override
    public void setVariable(String var, String value) {
        variablesMap.put(var, value);
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
