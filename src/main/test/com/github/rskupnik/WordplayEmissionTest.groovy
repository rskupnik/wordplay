package com.github.rskupnik

import com.github.rskupnik.objects.AnchoredObject
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
        WordplayOutput output = wordplay.process(input)

        then:
        output.getText() == "The magic stone was vibrating slightly."
        List<AnchoredObject> anchoredObjects = output.getAnchoredObjects()
        anchoredObjects.size() == 1
        AnchoredObject anchoredObject = anchoredObjects.get(0)
        anchoredObject.getText() == "vibrating"
        anchoredObject.getPosition() == input.indexOf('{')
        anchoredObject.getStringParam("effect") == "vibrate"
        anchoredObject.getStringParam("tint") == "blue"
    }
}
