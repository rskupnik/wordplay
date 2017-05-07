package com.github.rskupnik

import com.github.rskupnik.exceptions.WordplayInjectionException
import spock.lang.Specification
import spock.lang.Unroll

class WordplayInjectionTest extends Specification {

    private final Wordplay wordplay = new WordplayImpl()

    def setup() {
        wordplay.reset()
    }

    //region External Injection
    @Unroll
    def "should parse external injection: inj={>#_inj_}"() {
        given:
        String input = "There was a {>" + _inj_ + "} standing at the wall."

        when:
        wordplay.inject(_inj_, "tired guard")
        WordplayOutput output = wordplay.process(input)

        then:
        boolean result = output.getText() == "There was a tired guard standing at the wall."
        result == _expected_

        where:
        _expected_ | _inj_
        true       | " guard"
        true       | " guard that's tired"
        true       | " this is a very long variable name, no one should do this"
        true       | "guard"
    }

    def "should parse more than one external injection"() {
        given:
        String input = "There was a {> guard} standing at the {> wall}."

        when:
        wordplay.inject("guard", "tired guard")
        wordplay.inject("wall", "wall")
        WordplayOutput output = wordplay.process(input)

        then:
        output.getText() == "There was a tired guard standing at the wall."
    }

    def "should parse nested external injection"() {
        given:
        String input = "There was a {> guard} standing at the wall."

        when:
        wordplay.inject("guard", "{> cond} guard")
        wordplay.inject("cond", "tired")
        WordplayOutput output = wordplay.process(input)

        then:
        output.getText() == "There was a tired guard standing at the wall."
    }

    @Unroll
    def "should parse external injection embedded in ternary expression: var=#_var_"() {
        given:
        String input = "The weather was {{> var} ? {> result_true} | rainy}."

        when:
        wordplay.inject("var", "weather_sunny")
        wordplay.inject("result_true", "sunny")
        wordplay.setVariable("weather_sunny", _var_)
        WordplayOutput output = wordplay.process(input)

        then:
        output.getText() == _result_

        where:
        _result_                 | _var_
        "The weather was sunny." | true
        "The weather was rainy." | false
    }

    def "should parse ternary expression injected externally"() {
        given:
        String input = "The weather was {> weather}."

        when:
        wordplay.inject("weather", "{weather_sunny ? sunny | rainy}")
        wordplay.setVariable("weather_sunny", true)
        WordplayOutput output = wordplay.process(input)

        then:
        output.getText() == "The weather was sunny."
    }

    def "should parse internal injection embedded in external injection"() {
        given:
        String input = "The sky was {> sky}.\n"
                        +"\$\n"
                        +"> 0 clear"

        when:
        wordplay.inject("sky", "{> 0}")
        WordplayOutput output = wordplay.process(input)

        then:
        output.getText() == "The sky was clear."
    }

    def "should throw exception when external injection object is not provided"() {
        given:
        String input = "There was a {> guard} standing at the wall."

        when:
        wordplay.process(input)

        then:
        thrown(WordplayInjectionException)
    }
    //endregion

    //region Internal Injection
    def "should parse internal injection"() {
        given:
        String input = "The sky was {> 0}.\n"
                        +"\$\n"
                        +"> 0 clear"

        when:
        WordplayOutput output = wordplay.process(input)

        then:
        output.getText() == "The sky was clear."
    }

    def "should parse more than one internal injection"() {
        given:
        String input = "The sky was {> 0} on that {> 1}.\n"
                        +"\$\n"
                        +"> 0 clear\n"
                        +"> 1 day"

        when:
        WordplayOutput output = wordplay.process(input)

        then:
        output.getText() == "The sky was clear on that day."
    }

    def "should parse nested internal injection"() {
        given:
        String input = "The sky was {> 0}.\n"
                        +"\$\n"
                        +"> 0 {> 1}\n"
                        +"> 1 clear"

        when:
        WordplayOutput output = wordplay.process(input)

        then:
        output.getText() == "The sky was clear."
    }

    def "should parse nested internal injection with back-reference"() {
        given:
        String input = "The sky was {> 1}.\n"
                        +"\$\n"
                        +"> 0 clear\n"
                        +"> 1 {> 0}"

        when:
        WordplayOutput output = wordplay.process(input)

        then:
        output.getText() == "The sky was clear."
    }

    def "should parse internal injection embedded in ternary expression"() {
        given:
        String input = "The weather was {weather_sunny ? {> 0} | {> 1}}.\n"
                        +"\$\n"
                        +"> 0 sunny\n"
                        +"> 1 rainy"

        when:
        wordplay.setVariable("weather_sunny", true)
        WordplayOutput output = wordplay.process(input)

        then:
        output.getText() == "The weather was sunny."
    }

    def "should parse ternary expression injected internally"() {
        given:
        String input = "The weather was {> 0}.\n"
                        +"\$\n"
                        +"> 0 {weather_sunny ? sunny | rainy}"

        when:
        wordplay.setVariable("weather_sunny", true)
        WordplayOutput output = wordplay.process(input)

        then:
        output.getText() == "The weather was sunny."
    }

    def "should parse external injection embedded in internal injection"() {
        given:
        String input = "The sky was {> 0}.\n"
                        +"\$\n"
                        +"> 0 {> sky}"

        when:
        wordplay.inject("sky", "clear")
        WordplayOutput output = wordplay.process(input)

        then:
        output.getText() == "The sky was clear."
    }

    def "should throw exception when internal injection object is not provided"() {
        given:
        String input = "The sky was {> 0}.\n"
                        +"\$\n"

        when:
        wordplay.process(input)

        then:
        thrown(WordplayInjectionException)
    }
    //endregion
}
