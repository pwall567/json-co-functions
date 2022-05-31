/*
 * @(#) JSONCoFunctionsTest.java
 *
 * json-co-functions  Non-blocking functions for JSON formatting
 * Copyright (c) 2022 Peter Wall
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

package net.pwall.json

import kotlin.test.Test
import kotlin.test.expect
import kotlinx.coroutines.runBlocking
import net.pwall.json.JSONCoFunctions.coOutputChar
import net.pwall.json.JSONCoFunctions.coOutputString

class JSONCoFunctionsTest {

    @Test fun `should format string correctly`() = runBlocking {
        expect("\"hello\"") { coOutputStringCapture("hello") }
        expect("\"hello\\n\"") { coOutputStringCapture("hello\n") }
        expect("\"\"") { coOutputStringCapture("") }
        expect("\"mdash \\u2014 \\r\\n\"") { coOutputStringCapture("mdash \u2014 \r\n") }
        expect("\"mdash \u2014 \\r\\n\"") { coOutputStringCapture("mdash \u2014 \r\n", true) }
    }

    private suspend fun coOutputStringCapture(s: String, includeNonASCII: Boolean = false): String {
        val charArray = CharArray(40)
        var i = 0
        coOutputString(s, includeNonASCII) { charArray[i++] = it }
        return String(charArray, 0, i)
    }

    @Test fun `should format single char`() = runBlocking {
        expect("A") { coOutputCharCapture('A') }
        expect("\\b") { coOutputCharCapture('\b') }
        expect("\\f") { coOutputCharCapture('\u000C') }
        expect("\\n") { coOutputCharCapture('\n') }
        expect("\\r") { coOutputCharCapture('\r') }
        expect("\\t") { coOutputCharCapture('\t') }
        expect("\\u2014") { coOutputCharCapture('\u2014') }
        expect("\u2014") { coOutputCharCapture('\u2014', true) }
    }

    private suspend fun coOutputCharCapture(ch: Char, includeNonASCII: Boolean = false): String {
        val charArray = CharArray(8)
        var i = 0
        coOutputChar(ch, includeNonASCII) { charArray[i++] = it }
        return String(charArray, 0, i)
    }

}
