/*
 * @(#) JSONCoFunctions.java
 *
 * json-co-functions  Non-blocking functions for JSON formatting
 * Copyright (c) 2022, 2023 Peter Wall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.kstuff.json

import io.kstuff.util.CoIntOutput.output4HexLC
import io.kstuff.util.CoOutput
import io.kstuff.util.output

/**
 * Non-blocking functions to help with JSON string output.
 *
 * @author  Peter Wall
 */
object JSONCoFunctions {

    /**
     * Output a [CharSequence] in JSON quoted string form (applying JSON escaping rules).  The characters above the
     * ASCII range (`0x20` to `0x7E`) are output as Unicode escape sequences unless the `includeNonASCII` flag is set to
     * `true`.
     */
    suspend fun coOutputString(cs: CharSequence, includeNonASCII: Boolean = false, out: CoOutput) =
            out.outputString(cs, includeNonASCII)

    /**
     * Output a [CharSequence] in JSON quoted string form (applying JSON escaping rules).  The characters above the
     * ASCII range (`0x20` to `0x7E`) are output as Unicode escape sequences unless the `includeNonASCII` flag is set to
     * `true`.
     */
    suspend fun CoOutput.outputString(cs: CharSequence, includeNonASCII: Boolean = false) {
        output('"')
        for (i in cs.indices)
            outputChar(cs[i], includeNonASCII)
        output('"')
    }

    /**
     * Output a single character applying JSON escaping rules.  The characters above the ASCII range (`0x20` to `0x7E`)
     * are output as Unicode escape sequences unless the `includeNonASCII` flag is set to `true`.
     */
    suspend fun coOutputChar(ch: Char, includeNonASCII: Boolean = false, out: CoOutput) =
            out.outputChar(ch, includeNonASCII)

    /**
     * Output a single character applying JSON escaping rules.  The characters above the ASCII range (`0x20` to `0x7E`)
     * are output as Unicode escape sequences unless the `includeNonASCII` flag is set to `true`.
     */
    suspend fun CoOutput.outputChar(ch: Char, includeNonASCII: Boolean = false) {
        when {
            ch == '"' || ch == '\\' -> {
                output('\\')
                output(ch)
            }
            ch == '\b' -> {
                output('\\')
                output('b')
            }
            ch == '\u000C' -> {
                output('\\')
                output('f')
            }
            ch == '\n' -> {
                output('\\')
                output('n')
            }
            ch == '\r' -> {
                output('\\')
                output('r')
            }
            ch == '\t' -> {
                output('\\')
                output('t')
            }
            ch < ' ' || ch in '\u007F'..'\u009F' || ch >= '\u00A0' && !includeNonASCII -> {
                output('\\')
                output('u')
                output4HexLC(ch.code)
            }
            else -> output(ch)
        }
    }

}
