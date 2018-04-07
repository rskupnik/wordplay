package com.github.rskupnik.wordplay

import spock.lang.Specification

class WordplayPreprocessingTest extends Specification {

    private final Wordplay wordplay = new WordplayImpl()

    def setup() {
        wordplay.reset()
    }

    def "should extract headers"() {
        given:
        final String input = "Irrelevant text\n" +
                "!\$\n" +
                "key1 value1\n" +
                "key2 value2"

        when:
        final Map<String, String> headers = wordplay.extractHeaders(input)

        then:
        headers.size() == 2
        headers.get("key1").equals("value1")
        headers.get("key2").equals("value2")
    }
}
