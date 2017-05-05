package com.github.rskupnik

import com.github.rskupnik.exceptions.WordplayProcessingException
import com.github.rskupnik.exceptions.WordplaySyntaxException
import spock.lang.Specification
import spock.lang.Unroll

class WordplayTernaryTest extends Specification {

    final Wordplay wordplay = new WordplayImpl();

    def setup() {
        wordplay.reset()
    }

    //region Shorthand Ternary Expressions
    @Unroll
    def "should resolve shorthand ternary expression given variable: #_var_"() {
        given:
        String script = "It was a {weather_sunny ? sunny | rainy} day."

        when:
        wordplay.setVariable("weather_sunny", _var_);
        String output = wordplay.process(script)

        then:
        output.equals(_result_)

        where:
        _result_              | _var_
        "It was a sunny day." | true
        "It was a rainy day." | false
    }

    def "should throw SyntaxException if shorthand ternary expression is invalid"() {
        given:
        String script = "It was a {weather_sunny ? sunny | rainy | cloudy} day."

        when:
        wordplay.process(script)

        then:
        thrown(WordplaySyntaxException)
    }

    def "should resolve shorthand ternary expression with a space near | symbol"() {
        given:
        String script = "It was a {weather_sunny ? sunny  |  rainy}day."

        when:
        wordplay.setVariable("weather_sunny", true);
        String output = wordplay.process(script)

        then:
        output.equals("It was a sunny day.")
    }

    def "should resolve nested shorthand ternary expression"() {
        given:
        String script = "The color was {blue ? {light ? light | dark} blue | red}."

        when:
        wordplay.setVariable("blue", true)
        wordplay.setVariable("light", true)
        String output = wordplay.process(script)

        then:
        output.equals("The color was light blue.")
    }

    def "should not process nested shorthand ternary expression if not touched"() {
        given:
        String script = "The color was {blue ? {light ? light | dark} blue | red}."

        when:
        wordplay.setVariable("blue", false)
        String output = wordplay.process(script)

        then:
        output.equals("The color was red.")

        // If the nested expression was evaluated, exception would be thrown due to missing light variable
    }

    def "should throw exception if missing required variable in shorthand ternary expression"() {
        given:
        String script = "The color was {blue ? blue | red}."

        when:
        wordplay.process(script)

        then:
        thrown(WordplayProcessingException)
    }
    //endregion

    //region Full Ternary Expressions
    @Unroll
    def "should resolve full ternary expression when given variable: #_weatherVar_"() {
        given:
        String script = "The weather was {weather:sunny ? sunny | weather:rainy ? rainy | fine}."

        when:
        wordplay.setVariable("weather", _weatherVar_)
        String output = wordplay.process(script)

        then:
        output.equals(_result_)

        where:
        _result_                 | _weatherVar_
        "The weather was sunny." | "sunny"
        "The weather was rainy." | "rainy"
        "The weather was fine."  | "unrecognized"
        "The weather was fine."  | null
    }

    @Unroll
    def "should resolve full nested ternary expression: color:#_colorVar_, shade:#_shadeVar_"() {
        given:
        String script = "The color was {color:blue ? {shade:light ? light | shade:dark ? dark | unspecified} blue | red}."

        when:
        wordplay.setVariable("color", _colorVar_)
        wordplay.setVariable("shade", _shadeVar_)
        String output = wordplay.process(script)

        then:
        output.equals(_result_)

        where:
        _result_                          | _colorVar_ | _shadeVar_
        "The color was light blue."       | "blue"     | "light"
        "The color was dark blue."        | "blue"     | "dark"
        "The color was unspecified blue." | "blue"     | null
        "The color was unspecified blue." | "blue"     | "unspecified"
        "The color was red."              | "red"      | null
        "The color was red."              | null       | null
        "The color was red."              | "unknown"  | null
    }

    def "should not process nested full ternary expression if not touched"() {
        given:
        String script = "The color was {color:blue ? {light ? light | dark} blue | red}."

        when:
        wordplay.setVariable("color", "red")
        String output = wordplay.process(script)

        then:
        output.equals("The color was red.")

        // If the nested expression was evaluated, exception would be thrown due to missing light variable
    }

    //endregion
}
