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
package com.github.rskupnik.wordplay.output;

import java.util.*;

public final class WordplayOutput {

    private final String text;
    private final List<AnchoredObject> anchoredObjects = new ArrayList<>();
    private final List<MetaObject> metaObjects = new ArrayList<>();

    public WordplayOutput(String text) {
        this.text = text;
    }

    public WordplayOutput(String text, List<AnchoredObject> anchoredObjects, List<MetaObject> metaObjects) {
        this(text);
        if (anchoredObjects != null)
            this.anchoredObjects.addAll(anchoredObjects);
        if (metaObjects != null)
            this.metaObjects.addAll(metaObjects);
    }

    public final String getText() {
        return text;
    }

    public final List<AnchoredObject> getAnchoredObjects() {
        return Collections.unmodifiableList(anchoredObjects);
    }

    public final List<MetaObject> getMetaObjects() {
        return Collections.unmodifiableList(metaObjects);
    }
}
