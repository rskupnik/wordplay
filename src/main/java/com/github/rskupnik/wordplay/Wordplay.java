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
package com.github.rskupnik.wordplay;

import com.github.rskupnik.wordplay.exceptions.WordplayException;
import com.github.rskupnik.wordplay.output.WordplayOutput;

public interface Wordplay {
    WordplayOutput process(String input) throws WordplayException;
    void reset();
    void setVariable(String var, boolean value);
    void setVariable(String var, String value);
    void inject(String id, String value);
}
