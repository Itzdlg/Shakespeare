/*
 *   Copyright 2021 Dominick and contributors
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import me.schooltests.shakespeare.tokens.ScriptTokenizer
import me.schooltests.shakespeare.tokens.Token
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.StringReader
import java.util.*

class TokenizerTests {
    val baseScripts = arrayOf(
        """
        function main with String[] args:
            print "hello world"
            after 5 seconds:
                print "5 seconds have passed"
        """,
        """
        a:
        b:
            print "hello world"
        c:
        class Hello:
            Integer field balance
        """,
        """
        on start with {_args}:
            for {_arg} in {_args}:
                print "Argument %{_arg}%"
        """,
        """
        on start with {_args}:
            for {_arg} in {_args}:
                print "Argument %{_arg}
            print "Finished"
        """
    )

    @Test
    fun ScriptTokenizer_ensureAllBlocksClose() {
        for (script in baseScripts) {
            val reader = BufferedReader(StringReader(script))
            val tokens = ScriptTokenizer.tokenize(reader)
            reader.close()

            val blockStart = Collections.frequency(tokens, Token.BlockStart)
            val blockEnd = Collections.frequency(tokens, Token.BlockEnd)

            assert(blockStart == blockEnd)
        }
    }

    @Test
    fun ScriptTokenizer_ensureAllStringsClose() {
        for (script in baseScripts) {
            val reader = BufferedReader(StringReader(script))
            val tokens = ScriptTokenizer.tokenize(reader)
            reader.close()

            val stringStart = Collections.frequency(tokens, Token.StringStart)
            val stringEnd = Collections.frequency(tokens, Token.StringEnd)

            assert(stringStart == stringEnd)
        }
    }

    @Test
    fun ScriptTokenizer_ensureAllInterpolatedStatementsClose() {
        for (script in baseScripts) {
            val reader = BufferedReader(StringReader(script))
            val tokens = ScriptTokenizer.tokenize(reader)
            reader.close()

            val interpolationStart = Collections.frequency(tokens, Token.InterpolationStart)
            val interpolationEnd = Collections.frequency(tokens, Token.InterpolationEnd)

            assert(interpolationStart == interpolationEnd)
        }
    }

    @Test
    fun ScriptTokenizer_ensureAllVariablesClose() {
        for (script in baseScripts) {
            val reader = BufferedReader(StringReader(script))
            val tokens = ScriptTokenizer.tokenize(reader)
            reader.close()

            val variableStart = Collections.frequency(tokens, Token.VariableStart)
            val variableEnd = Collections.frequency(tokens, Token.VariableEnd)

            assert(variableStart == variableEnd)
        }
    }
}