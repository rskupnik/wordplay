package com.github.rskupnik.wordplay

import com.github.rskupnik.wordplay.output.MetaMap
import com.github.rskupnik.wordplay.output.MetaObject
import com.github.rskupnik.wordplay.output.WordplayOutput
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

    def "should emit map with header section present"() {
        given:
        final String input = "Irrelevant text\n" +
                "\$\n" +
                "<m map key:val|key2:val2\n" +
                "!\$\n" +
                "key1 value1\n" +
                "key2 value2"

        when:
        final Map<String, String> headers = wordplay.extractHeaders(input)
        final WordplayOutput output = wordplay.process(input)

        then:
        List<MetaObject> metaObjects = output.getMetaObjects()
        metaObjects.size() == 1
        MetaObject mobj = metaObjects.get(0)
        mobj instanceof MetaMap
        MetaMap mmap = (MetaMap) mobj
        mmap.getString("key").equals("val")
        mmap.getString("key2").equals("val2")
        headers.size() == 2
        headers.get("key1").equals("value1")
        headers.get("key2").equals("value2")
    }

    def "should extract headers for \\r\\n ending"() {
        given:
        final String input = "Irrelevant text\r\n" +
                "!\$\r\n" +
                "key1 value1\r\n" +
                "key2 value2"

        when:
        final Map<String, String> headers = wordplay.extractHeaders(input)

        then:
        headers.size() == 2
        headers.get("key1").equals("value1")
        headers.get("key2").equals("value2")
    }

    def "should extract headers for \\r ending"() {
        given:
        final String input = "Irrelevant text\r" +
                "!\$\r" +
                "key1 value1\r" +
                "key2 value2"

        when:
        final Map<String, String> headers = wordplay.extractHeaders(input)

        then:
        headers.size() == 2
        headers.get("key1").equals("value1")
        headers.get("key2").equals("value2")
    }
}
