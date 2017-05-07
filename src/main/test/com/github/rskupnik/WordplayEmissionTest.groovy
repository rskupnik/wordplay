package com.github.rskupnik

import spock.lang.Specification

class WordplayEmissionTest extends Specification {

    private final Wordplay wordplay = new WordplayImpl()

    def setup() {
        wordplay.reset()
    }

    def "should emit an anchored object"() {
        given:
        String input = "The magic stone was {< effect:vibrate|tint:blue vibrating} slightly."

        when:
        String output = wordplay.process(input)

        then:
        output == "The magic stone was vibrating slightly."
    }
}
