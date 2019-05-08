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

import com.github.rskupnik.wordplay.output.MetaObject
import com.github.rskupnik.wordplay.output.WordplayOutput
import com.github.rskupnik.wordplay.output.AnchoredObject
import com.github.rskupnik.wordplay.output.MetaList
import com.github.rskupnik.wordplay.output.MetaMap
import spock.lang.Specification
import spock.lang.Unroll

class WordplayFullTest extends Specification {

    private final Wordplay wordplay = new WordplayImpl()

    def setup() {
        wordplay.reset()
    }

    @Unroll
    def "should parse a properly prepared script with #_escaped_ line ending"() {
        given:
        String input = "It was a {weather_sunny ? sunny | rainy} day." + _le_ +
                "There was a {> guard} standing at the wall." + _le_ +
                "The sky was {> 1}." + _le_ +
                "In the middle of the room stood a {color:brown brown |:blue blue | black} table." + _le_ +
                "The {< tint:blue magic stone} was {> 2} slightly." + _le_ +
                "\$" + _le_ +
                "> 1 {weather_sunny ? clear | clouded}" + _le_ +
                "> 2 {< effect:vibrate|tint:blue vibrating}" + _le_ +
                "<l mobs Dreadful Vampire|Fearful Wolf" + _le_ +
                "<m params fighting_allowed:true|escape_allowed:false"
        String expected = "It was a sunny day." + _le_ +
                "There was a tall guard standing at the wall." + _le_ +
                "The sky was clear." + _le_ +
                "In the middle of the room stood a black table." + _le_ +
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

        where:
        _le_    |   _escaped_
        "\n"    |   "\\n"
        "\r\n"  |   "\\r\\n"
        "\r"    |   "\\r"
    }
}
