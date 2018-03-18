/*
 Wordplay - text processing engine for dynamic storytelling
 Copyright (C) 2017  Radoslaw Skupnik

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.rskupnik.wordplay

import com.github.rskupnik.wordplay.exceptions.WordplayProcessingException
import com.github.rskupnik.wordplay.output.WordplayOutput
import spock.lang.Specification
import spock.lang.Unroll

class WordplayExpressionTest extends Specification {

    private final Wordplay wordplay = new WordplayImpl();

    def setup() {
        wordplay.reset()
    }

    //region Ternary Expressions
    @Unroll
    def "should resolve ternary expression given variable: #_var_"() {
        given:
        String script = "It was a {weather_sunny ? sunny | rainy} day."

        when:
        wordplay.setVariable("weather_sunny", _var_);
        WordplayOutput output = wordplay.process(script)

        then:
        output.getText().equals(_result_)

        where:
        _result_              | _var_
        "It was a sunny day." | true
        "It was a rainy day." | false
        "It was a rainy day." | null
    }

    def "should throw SyntaxException if ternary expression is invalid"() {
        given:
        String script = "It was a {weather_sunny ?? sunny | rainy | cloudy} day."

        when:
        wordplay.process(script)

        then:
        thrown(WordplayProcessingException)
    }

    def "should resolve ternary expression with a space near | symbol"() {
        given:
        String script = "It was a {weather_sunny ? sunny  |  rainy}day."

        when:
        wordplay.setVariable("weather_sunny", true);
        WordplayOutput output = wordplay.process(script)

        then:
        output.getText().equals("It was a sunny day.")
    }

    def "should resolve nested ternary expression"() {
        given:
        String script = "The color was {blue ? {light ? light | dark} blue | red}."

        when:
        wordplay.setVariable("blue", true)
        wordplay.setVariable("light", true)
        WordplayOutput output = wordplay.process(script)

        then:
        output.getText().equals("The color was light blue.")
    }

    def "should not process nested ternary expression if not touched"() {
        given:
        String script = "The color was {blue ? {light ? light | dark} blue | red}."

        when:
        wordplay.setVariable("blue", false)
        WordplayOutput output = wordplay.process(script)

        then:
        output.getText().equals("The color was red.")

        // If the nested expression was evaluated, exception would be thrown due to missing light variable
    }

    def "should default to false if missing variable in ternary expression"() {
        given:
        String script = "The color was {blue ? blue | red}."

        when:
        WordplayOutput output = wordplay.process(script)

        then:
        output.getText().equals("The color was red.");
    }
    //endregion

    //region Matching Expressions
    @Unroll
    def "should resolve matching expression when given variable: #_weatherVar_"() {
        given:
        String script = "The weather was {weather:sunny sunny |:rainy rainy | fine}."

        when:
        wordplay.setVariable("weather", _weatherVar_)
        WordplayOutput output = wordplay.process(script)

        then:
        output.getText().equals(_result_)

        where:
        _result_                 | _weatherVar_
        "The weather was sunny." | "sunny"
        "The weather was rainy." | "rainy"
        "The weather was fine."  | "unrecognized"
        "The weather was fine."  | null
    }

    @Unroll
    def "should resolve matching expression with multiple options when given variable: #_weatherVar_"() {
        given:
        String script = "The weather was {weather:sunny sunny |:rainy rainy |:cloudy cloudy | fine}."

        when:
        wordplay.setVariable("weather", _weatherVar_)
        WordplayOutput output = wordplay.process(script)

        then:
        output.getText().equals(_result_)

        where:
        _result_                  | _weatherVar_
        "The weather was sunny."  | "sunny"
        "The weather was rainy."  | "rainy"
        "The weather was cloudy." | "cloudy"
        "The weather was fine."   | "unrecognized"
    }

    @Unroll
    def "should resolve nested matching expression with multiple options: color:#_colorVar_, shade:#_shadeVar_"() {
        given:
        String script = "The color was {color:blue {shade:light light |:dark dark |:dim dim | unspecified} blue |:green green | red}."

        when:
        wordplay.setVariable("color", _colorVar_)
        wordplay.setVariable("shade", _shadeVar_)
        WordplayOutput output = wordplay.process(script)

        then:
        output.getText().equals(_result_)

        where:
        _result_                          | _colorVar_ | _shadeVar_
        "The color was light blue."       | "blue"     | "light"
        "The color was dark blue."        | "blue"     | "dark"
        "The color was dim blue."         | "blue"     | "dim"
        "The color was green."            | "green"    | "dim"
        "The color was unspecified blue." | "blue"     | "unspecified"
        "The color was red."              | "red"      | "unspecified"
        "The color was red."              | "unknown"  | "unspecified"
        "The color was green."            | "green"    | "unspecified"
    }

    @Unroll
    def "should resolve nested matching expression: color:#_colorVar_, shade:#_shadeVar_"() {
        given:
        String script = "The color was {color:blue {shade:light light |:dark dark | unspecified} blue | red}."

        when:
        wordplay.setVariable("color", _colorVar_)
        wordplay.setVariable("shade", _shadeVar_)
        WordplayOutput output = wordplay.process(script)

        then:
        output.getText().equals(_result_)

        where:
        _result_                          | _colorVar_ | _shadeVar_
        "The color was light blue."       | "blue"     | "light"
        "The color was dark blue."        | "blue"     | "dark"
        "The color was unspecified blue." | "blue"     | "unspecified"
        "The color was red."              | "red"      | "unspecified"
        "The color was red."              | "unknown"  | "unspecified"
    }

    def "should default to false when missing variable in matching expression"() {
        given:
        String script = "The weather was {weather:sunny sunny |:rainy rainy | fine}."

        when:
        WordplayOutput output = wordplay.process(script)

        then:
        output.getText().equals("The weather was fine.")
    }
    //endregion
}
