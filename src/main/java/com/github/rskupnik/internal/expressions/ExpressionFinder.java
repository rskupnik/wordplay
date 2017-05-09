package com.github.rskupnik.internal.expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ExpressionFinder {

    private Pattern pattern = Pattern.compile("\\{>\\s?(\\S*)\\s?}");

    /**
     * Finds all injection expressions in a given String.
     * Injection Expressions are within {> and }
     */
    public List<InjectionExpression> findInjectionExpressions(String text) {
        List<InjectionExpression> output = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            output.add(new InjectionExpression(matcher.group(), matcher.start(), matcher.end(), matcher.group(1)));
            System.out.println(matcher.group());
            System.out.println(matcher.group(1));
        }
        return output;
    }

    public static void main(String[] args) {
        new ExpressionFinder().findInjectionExpressions("This is a test {> {> doubleyo} yo }.");
    }
}
