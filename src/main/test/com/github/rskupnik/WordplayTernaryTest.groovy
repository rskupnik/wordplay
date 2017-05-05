package com.github.rskupnik

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
    //endregion

}
