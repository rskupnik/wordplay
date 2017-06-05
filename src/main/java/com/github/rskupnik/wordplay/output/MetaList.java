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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MetaList extends MetaObject {

    private final List<String> data = new ArrayList<>();

    public MetaList(String id, List<String> data) {
        super(id);
        this.data.addAll(data);
    }

    public final List<String> getData() {
        return Collections.unmodifiableList(data);
    }

    public final Object getObject(int index) {
        return data.get(index);
    }

    public final String getString(int index) {
        return data.get(index);
    }

    public final int getInt(int index) {
        Integer out = null;
        try {
            out = Integer.parseInt(data.get(index));
        } finally {
            return out;
        }
    }

    public final boolean getBool(int index) {
        return Boolean.parseBoolean(data.get(index));
    }
}
