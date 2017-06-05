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

import java.util.HashMap;
import java.util.Map;

public final class AnchoredObject {

    private final String text;
    private final int position;
    private final Map<String, String> parameters = new HashMap<>();

    public AnchoredObject(String text, int position) {
        this.text = text;
        this.position = position;
    }

    public AnchoredObject(String text, int position, Map<String, String> parameters) {
        this(text, position);
        this.parameters.putAll(parameters);
    }

    public String getText() {
        return text;
    }

    public int getPosition() {
        return position;
    }

    public Object getParam(String key) {
        return parameters.get(key);
    }

    public boolean getBoolParam(String key) {
        return Boolean.parseBoolean(parameters.get(key));
    }

    public String getStringParam(String key) {
        return (String) parameters.get(key);
    }

    public Integer getIntParam(String key) {
        Integer out = null;
        try {
            out = Integer.parseInt(parameters.get(key));
        } finally {
            return out;
        }
    }
}
