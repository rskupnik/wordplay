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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MetaMap extends MetaObject {

    private final Map<String, String> data = new HashMap<>();

    public MetaMap(String id, Map<String, String> data) {
        super(id);
        this.data.putAll(data);
    }

    public final Map<String, String> getData() {
        return Collections.unmodifiableMap(data);
    }

    public final Object getObject(String key) {
        return data.get(key);
    }

    public final String getString(String key) {
        return data.get(key);
    }

    public final Integer getInt(String key) {
        Integer out = null;
        try {
            out = Integer.parseInt(data.get(key));
        } finally {
            return out;
        }
    }

    public final boolean getBool(String key) {
        return Boolean.parseBoolean(data.get(key));
    }
}
