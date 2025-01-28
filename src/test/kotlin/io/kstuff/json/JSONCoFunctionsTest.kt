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

package io.kstuff.json

import kotlin.test.Test
import kotlinx.coroutines.runBlocking

import io.kstuff.test.shouldBe
import io.kstuff.util.CoOutput

import io.kstuff.json.JSONCoFunctions.coOutputChar
import io.kstuff.json.JSONCoFunctions.coOutputString
import io.kstuff.json.JSONCoFunctions.outputChar
import io.kstuff.json.JSONCoFunctions.outputString

class JSONCoFunctionsTest {

    @Test fun `should format string correctly using lambda`() = runBlocking {
        coOutputStringCapture("hello") shouldBe "\"hello\""
        coOutputStringCapture("hello\n") shouldBe "\"hello\\n\""
        coOutputStringCapture("") shouldBe "\"\""
        coOutputStringCapture("mdash \u2014 \r\n") shouldBe "\"mdash \\u2014 \\r\\n\""
        coOutputStringCapture("euro \u20AC \r\n") shouldBe "\"euro \\u20ac \\r\\n\""
        coOutputStringCapture("mdash \u2014 \r\n", true) shouldBe "\"mdash \u2014 \\r\\n\""
    }

    private suspend fun coOutputStringCapture(s: String, includeNonASCII: Boolean = false): String {
        val charArray = CharArray(40)
        var i = 0
        coOutputString(s, includeNonASCII) { charArray[i++] = it }
        return String(charArray, 0, i)
    }

    @Test fun `should format string correctly using extension function`() = runBlocking {
        outputStringCapture("hello") shouldBe "\"hello\""
        outputStringCapture("hello\n") shouldBe "\"hello\\n\""
        outputStringCapture("") shouldBe "\"\""
        outputStringCapture("mdash \u2014 \r\n") shouldBe "\"mdash \\u2014 \\r\\n\""
        outputStringCapture("euro \u20AC \r\n") shouldBe "\"euro \\u20ac \\r\\n\""
        outputStringCapture("mdash \u2014 \r\n", true) shouldBe "\"mdash \u2014 \\r\\n\""
    }

    private suspend fun outputStringCapture(s: String, includeNonASCII: Boolean = false) =
            CoCapture().apply { outputString(s, includeNonASCII) }.toString()

    @Test fun `should format single char using lambda`() = runBlocking {
        coOutputCharCapture('A') shouldBe "A"
        coOutputCharCapture('\b') shouldBe "\\b"
        coOutputCharCapture('\u000C') shouldBe "\\f"
        coOutputCharCapture('\n') shouldBe "\\n"
        coOutputCharCapture('\r') shouldBe "\\r"
        coOutputCharCapture('\t') shouldBe "\\t"
        coOutputCharCapture('\u2014') shouldBe "\\u2014"
        coOutputCharCapture('\u20AC') shouldBe "\\u20ac"
        coOutputCharCapture('\u2014', true) shouldBe "\u2014"
    }

    private suspend fun coOutputCharCapture(ch: Char, includeNonASCII: Boolean = false): String {
        val charArray = CharArray(8)
        var i = 0
        coOutputChar(ch, includeNonASCII) { charArray[i++] = it }
        return String(charArray, 0, i)
    }

    @Test fun `should format single char using extension function`() = runBlocking {
        outputCharCapture('A') shouldBe "A"
        outputCharCapture('\b') shouldBe "\\b"
        outputCharCapture('\u000C') shouldBe "\\f"
        outputCharCapture('\n') shouldBe "\\n"
        outputCharCapture('\r') shouldBe "\\r"
        outputCharCapture('\t') shouldBe "\\t"
        outputCharCapture('\u2014') shouldBe "\\u2014"
        outputCharCapture('\u20AC') shouldBe "\\u20ac"
        outputCharCapture('\u2014', true) shouldBe "\u2014"
    }

    private suspend fun outputCharCapture(ch: Char, includeNonASCII: Boolean = false) =
            CoCapture().apply { outputChar(ch, includeNonASCII) }.toString()

    class CoCapture(size: Int = 256) : CoOutput {

        private val array = CharArray(size)
        private var index = 0

        override suspend fun invoke(ch: Char) {
            array[index++] = ch
        }

        override fun toString() = String(array, 0, index)

    }

}
