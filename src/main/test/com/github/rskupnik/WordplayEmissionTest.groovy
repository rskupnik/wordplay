package com.github.rskupnik

import com.github.rskupnik.exceptions.WordplaySyntaxException
import com.github.rskupnik.output.AnchoredObject
import com.github.rskupnik.output.MetaList
import com.github.rskupnik.output.MetaMap
import com.github.rskupnik.output.MetaObject
import com.github.rskupnik.output.WordplayOutput
import spock.lang.Specification
import spock.lang.Unroll

class WordplayEmissionTest extends Specification {

    private final Wordplay wordplay = new WordplayImpl()

    def setup() {
        wordplay.reset()
    }

    //region Anchored objects
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

    def "should emit more than one anchored object"() {
        given:
        String input = "The magic {< tint:grey stone} was {< effect:vibrate|tint:blue vibrating} slightly."

        when:
        WordplayOutput output = wordplay.process(input)

        then:
        output.getText() == "The magic stone was vibrating slightly."
        List<AnchoredObject> anchoredObjects = output.getAnchoredObjects()
        anchoredObjects.size() == 2
        AnchoredObject anchoredObject = anchoredObjects.get(0)
        anchoredObject.getText() == "vibrating"
        anchoredObject.getStringParam("effect") == "vibrate"
        anchoredObject.getStringParam("tint") == "blue"
        anchoredObject anchoredObject = anchoredObjects.get(1)
        anchoredObject.getText() == "stone"
        anchoredObject.getStringParam("tint") == "grey"
    }

    def "should emit an anchored object inside ternary expression"() {
        given:
        String input = "The magic stone was {vibrate ? {< effect:vibrate vibrating} slightly | calm}."

        when:
        wordplay.setVariable("vibrate", true)
        WordplayOutput output = wordplay.process(input)

        then:
        output.getText() == "The magic stone was vibrating slightly."
        List<AnchoredObject> anchoredObjects = output.getAnchoredObjects()
        anchoredObjects.size() == 1
        AnchoredObject anchoredObject = anchoredObjects.get(0)
        anchoredObject.getText() == "vibrating"
        anchoredObject.getStringParam("effect") == "vibrate"
    }

    def "should not emit an anchored object inside ternary expression that is not evaluated"() {
        given:
        String input = "The magic stone was {vibrate ? {< effect:vibrate vibrating} slightly | calm}."

        when:
        wordplay.setVariable("vibrate", false)
        WordplayOutput output = wordplay.process(input)

        then:
        output.getText() == "The magic stone was calm."
        List<AnchoredObject> anchoredObjects = output.getAnchoredObjects()
        anchoredObjects.size() == 0
    }

    def "should emit an anchored object injected externally"() {
        given:
        String input = "The magic stone was {> inject} slightly."

        when:
        wordplay.inject("inject", "{< effect:vibrate vibrating}")
        WordplayOutput output = wordplay.process(input)

        then:
        output.getText() == "The magic stone was vibrating slightly."
        List<AnchoredObject> anchoredObjects = output.getAnchoredObjects()
        anchoredObjects.size() == 1
        AnchoredObject anchoredObject = anchoredObjects.get(0)
        anchoredObject.getText() == "vibrating"
        anchoredObject.getStringParam("effect") == "vibrate"
    }

    def "should emit an anchored object injected internally"() {
        given:
        String input = "The magic stone was {> 0} slightly.\n" +
                "\$\n" +
                "> 0 {< effect:vibrate vibrating}"

        when:
        WordplayOutput output = wordplay.process(input)

        then:
        output.getText() == "The magic stone was vibrating slightly."
        List<AnchoredObject> anchoredObjects = output.getAnchoredObjects()
        anchoredObjects.size() == 1
        AnchoredObject anchoredObject = anchoredObjects.get(0)
        anchoredObject.getText() == "vibrating"
        anchoredObject.getStringParam("effect") == "vibrate"
    }

    @Unroll
    def "should throw exception when syntax is invalid: expr=#_expr_"() {
        given:
        String input = "The magic stone was " + _expr_ + " slightly."

        when:
        wordplay.process(input)

        then:
        thrown(_exception_)

        where:
        _exception_             | _expr_
        WordplaySyntaxException | "{ effect:vibrate vibrating }"
        WordplaySyntaxException | "{< effect::vibrate vibrating }"
        WordplaySyntaxException | "{< effect;vibrate vibrating }"
        WordplaySyntaxException | "{< effect|vibrate vibrating }"
        WordplaySyntaxException | "{< effect,vibrate vibrating }"
        WordplaySyntaxException | "{< effect,vibrate vibrating "
        WordplaySyntaxException | "< effect,vibrate vibrating }"
        WordplaySyntaxException | "effect,vibrate vibrating }"
    }
    //endregion

    //region Non-anchored objects
    @Unroll
    def "should emit a non-anchored map: obj=#_obj_"() {
        given:
        String input = "Some random text.\n" +
                "\$\n" +
                "<m map " + _obj_

        when:
        WordplayOutput output = wordplay.process(input)

        then:
        output.getText() == "Some random text."
        List<MetaObject> metaObjects = output.getMetaObjects()
        metaObjects.size() == 1
        MetaObject mobj = metaObjects.get(0)
        mobj instanceof MetaMap
        MetaMap mmap = (MetaMap) mobj
        mmap.getObject("key").equals(_value1_)
        mmap.getObject("key2").equals(_value2_)

        where:
        _obj_                            | _value1_  | _value2_
        "key:astring|key2:anotherstring" | "astring" | "anotherstring"
        "key:false|key2:true"            | "false"   | "true"
        "key:5|key2:-2154"               | "5"       | "-2154"
        "key:null|key2:0"                | "null"    | "0"
    }

    @Unroll
    def "should emit a non-anchored list: obj=#_obj_"() {
        given:
        String input = "Some random text.\n" +
                "\$\n" +
                "< list " + _obj_

        when:
        WordplayOutput output = wordplay.process(input)

        then:
        output.getText() == "Some random text."
        List<MetaObject> metaObjects = output.getMetaObjects()
        metaObjects.size() == 1
        MetaObject mobj = metaObjects.get(0)
        mobj instanceof MetaList
        MetaList mlist = (MetaList) mobj
        mlist.getObject(0).equals(_value1_)
        mlist.getObject(1).equals(_value2_)

        where:
        _obj_        | _value1_ | _value2_
        "one,two"    | "one"    | "two"
        "true,false" | "true"   | "false"
        "5,-124124"  | "5"      | "-124124"
        "null,0"     | "null"   | "0"
    }

    @Unroll
    def "should throw exception when emitting non-anchored object with syntax error: obj=#_obj_"() {
        given:
        String input = "Some random text.\n" +
                "\$\n" + _obj_

        when:
        wordplay.process(input)

        then:
        thrown(_exception_)

        where:
        _exception_             | _obj_
        WordplaySyntaxException | " key,value"
        WordplaySyntaxException | "< key,value"
        WordplaySyntaxException | "< eist key;value|key2;value2"
        WordplaySyntaxException | "< eist key:value|key2;value2"
        WordplaySyntaxException | "< eist keyvalue|key2value2"
        WordplaySyntaxException | "eist keyvalue|key2value2"
        WordplaySyntaxException | " eist keyvalue|key2value2"
    }
    //endregion
}
