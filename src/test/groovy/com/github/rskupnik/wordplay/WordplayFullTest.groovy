package com.github.rskupnik.wordplay

import com.github.rskupnik.wordplay.output.MetaObject
import com.github.rskupnik.wordplay.output.WordplayOutput
import com.github.rskupnik.wordplay.output.AnchoredObject
import com.github.rskupnik.wordplay.output.MetaList
import com.github.rskupnik.wordplay.output.MetaMap
import spock.lang.Specification

class WordplayFullTest extends Specification {

    private final Wordplay wordplay = new WordplayImpl()

    def setup() {
        wordplay.reset()
    }

    def "should parse a properly prepared script"() {
        given:
        String input = "It was a {weather_sunny ? sunny | rainy} day.\n" +
                "There was a {> guard} standing at the wall.\n" +
                "The sky was {> 1}.\n" +
                "In the middle of the room stood a {color:brown brown |:blue blue | black} table.\n" +
                "The {< tint:blue magic stone} was {> 2} slightly.\n" +
                "\$\n" +
                "> 1 {weather_sunny ? clear | clouded}\n" +
                "> 2 {< effect:vibrate|tint:blue vibrating}\n" +
                "<l mobs Dreadful Vampire|Fearful Wolf\n" +
                "<m params fighting_allowed:true|escape_allowed:false"
        String expected = "It was a sunny day.\n" +
                "There was a tall guard standing at the wall.\n" +
                "The sky was clear.\n" +
                "In the middle of the room stood a black table.\n" +
                "The magic stone was vibrating slightly."

        when:
        wordplay.setVariable("weather_sunny", true)
        wordplay.setVariable("color", "red")
        wordplay.inject("guard", "tall guard")
        WordplayOutput output = wordplay.process(input)

        then:
        output.getText().equals(expected)
        output.getAnchoredObjects().size() == 2
        output.getMetaObjects().size() == 2
        AnchoredObject ao1 = output.getAnchoredObjects().get(0)
        ao1.getStringParam("tint").equals("blue")
        ao1.getText().equals("magic stone")
        ao1.getPosition() == expected.indexOf("magic stone")
        AnchoredObject ao2 = output.getAnchoredObjects().get(1)
        ao2.getStringParam("tint").equals("blue")
        ao2.getStringParam("effect").equals("vibrate")
        ao2.getText().equals("vibrating")
        ao2.getPosition() == expected.indexOf("vibrating")
        MetaObject mo1 = output.getMetaObjects().get(0)
        mo1 instanceof MetaList
        MetaList ml = (MetaList) mo1
        ml.getData().size() == 2
        ml.getData().get(0).equals("Dreadful Vampire")
        ml.getData().get(1).equals("Fearful Wolf")
        ml.getId().equals("mobs")
        MetaObject mo2 = output.getMetaObjects().get(1)
        mo2 instanceof MetaMap
        MetaMap mp = (MetaMap) mo2
        mp.getData().size() == 2
        mp.getId().equals("params")
        mp.getBool("fighting_allowed") == true
        mp.getBool("escape_allowed") == false
        mp.getString("escape_allowed").equals("false")
    }
}
