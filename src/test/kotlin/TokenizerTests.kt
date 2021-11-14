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