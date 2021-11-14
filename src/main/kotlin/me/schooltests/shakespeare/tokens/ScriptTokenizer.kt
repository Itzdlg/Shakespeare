package me.schooltests.shakespeare.tokens

import java.io.BufferedReader
import java.util.*

object ScriptTokenizer {
    private enum class Mode {
        STRING, VARIABLE, INTERPOLATION
    }

    fun tokenize(
        reader: BufferedReader
    ): List<Token> {
        val tokens = mutableListOf<Token>()

        var continueFor = 0 // Numbers of characters to skip, decrements each time
        var escaped = false // Whether some special characters should be escaped into Token.CHARACTER

        var modeComment = false // Whether a multi-line comment is entered
        val modeStack = LinkedList<Mode>()

        var foundIndent = false // Whether the indent-size has been determined
        var indentSize = 0 // The indent-size (in spaces, 4 is a tab)

        var openBlocks = 0 // The numbers of blocks, specifically ones that haven't been closed yet

        lineLoop@for (fullLine in reader.lineSequence()) {
            val lineContent = fullLine.trimStart().trimEnd()

            // Calculate the number of spaces in one indent
            if (!foundIndent && lineContent != fullLine) {
                if (fullLine.startsWith('\t')) {
                    foundIndent = true
                    indentSize = 4
                } else {
                    for (char in fullLine.toCharArray()) {
                        if (char == ' ')
                            indentSize++
                        else {
                            foundIndent = true
                            break
                        }
                    }
                }
            }

            // The current level, 0 for root, 1 for an immediate child, etc. Will never be higher than the number of opened blocks
            val level = if (foundIndent) {
                getIndentationLevel(fullLine, indentSize).coerceAtMost(openBlocks)
            } else 0


            // Close any blocks that have been exited (determined through indentation)
            val blocksToClose = openBlocks - level
            if (blocksToClose > 0) {
                openBlocks -= blocksToClose
                for (i in 1..blocksToClose)
                    tokens.add(Token.BlockEnd)
            }

            val characters = lineContent.toCharArray()
            for (i in characters.withIndex()) {
                val index = i.index
                val char = i.value

                // Skip any characters that have already been seen with a #lookahead()
                if (continueFor > 0) {
                    continueFor--
                    continue
                }

                // Handle multi-line comments
                if (modeComment) {
                    if (lookahead(characters, index, 2) == "*/") { // Whether the multi-line comment is exited
                        continueFor = 1
                        continue
                    }

                    continue
                }

                // Handle the opening of multi-line comments
                if (lookahead(characters, index, 2) == "/*") {
                    modeComment = true
                    continueFor = 1
                    continue
                }

                // Handle single-line comments
                if (lookahead(characters, index, 2) == "//") {
                    if (!firstTokenOfLine(tokens))
                        tokens.add(Token.EndStatement)
                    continue@lineLoop // Proceed to the next line
                }

                if (char == ' ') {
                    if (modeStack.peek() == Mode.STRING) {
                        tokens.add(Token.Character(char))
                        continue
                    }

                    if (firstTokenOfLine(tokens)) continue

                    tokens.add(Token.Space)
                    escaped = false
                    continue
                }

                if (char.isDigit()) {
                    if (modeStack.peek() == Mode.STRING) {
                        tokens.add(Token.Character(char))
                        continue
                    }

                    tokens.add(Token.Digit(char))
                    continue
                }

                // Handle escape-able characters
                if (escaped) {
                    tokens.add(Token.Character(char))
                    continue
                }

                if (char == '\\') {
                    escaped = true
                    continue
                }

                // Handle the opening of new blocks
                if (char == ':') {
                    if (modeStack.isNotEmpty()) {
                        tokens.add(Token.Character(char))
                        continue
                    }

                    tokens.add(Token.BlockStart)
                    openBlocks++
                    continue
                }

                if (char == '.') {
                    if (modeStack.peek() == Mode.STRING) {
                        tokens.add(Token.Character(char))
                        continue
                    }

                    tokens.add(Token.Period)
                    continue
                }

                if (char == ',') {
                    if (modeStack.peek() == Mode.STRING) {
                        tokens.add(Token.Character(char))
                        continue
                    }

                    tokens.add(Token.Comma)
                    continue
                }

                if (char == '<') {
                    if (modeStack.peek() == Mode.STRING) {
                        tokens.add(Token.Character(char))
                        continue
                    }

                    tokens.add(Token.LeftArrow)
                    continue
                }

                if (char == '>') {
                    if (modeStack.peek() == Mode.STRING) {
                        tokens.add(Token.Character(char))
                        continue
                    }

                    tokens.add(Token.RightArrow)
                    continue
                }

                if (char == '(') {
                    if (modeStack.peek() == Mode.STRING) {
                        tokens.add(Token.Character(char))
                        continue
                    }

                    tokens.add(Token.LeftParenthesis)
                    continue
                }

                if (char == ')') {
                    if (modeStack.peek() == Mode.STRING) {
                        tokens.add(Token.Character(char))
                        continue
                    }

                    tokens.add(Token.RightParenthesis)
                    continue
                }

                if (char == '[') {
                    if (modeStack.peek() == Mode.STRING) {
                        tokens.add(Token.Character(char))
                        continue
                    }

                    tokens.add(Token.LeftBracket)
                    continue
                }

                if (char == ']') {
                    if (modeStack.peek() == Mode.STRING) {
                        tokens.add(Token.Character(char))
                        continue
                    }

                    tokens.add(Token.RightBracket)
                    continue
                }

                if (char == '{') {
                    if (modeStack.peek() == Mode.STRING) {
                        tokens.add(Token.Character(char))
                        continue
                    }

                    tokens.add(Token.VariableStart)
                    modeStack.push(Mode.VARIABLE)
                    continue
                }

                if (char == '}') {
                    if (modeStack.peek() != Mode.VARIABLE) {
                        tokens.add(Token.Character(char))
                        continue
                    }

                    tokens.add(Token.VariableEnd)
                    modeStack.pop()
                    continue
                }

                // Handle interpolation (available only in variables or strings)
                if (char == '%') {
                    // Handle the closing of interpolation symbols
                    if (modeStack.peek() == Mode.INTERPOLATION) {
                        tokens.add(Token.InterpolationEnd)
                        modeStack.pop()
                        continue
                    }

                    // Add a plain Character token if the mode isn't valid for interpolation
                    if (modeStack.peek() != Mode.VARIABLE && modeStack.peek() != Mode.STRING) {
                        tokens.add(Token.Character(char))
                        continue
                    }

                    tokens.add(Token.InterpolationStart)
                    modeStack.push(Mode.INTERPOLATION)
                    continue
                }

                // Handle string literals (not available in variables)
                if (char == '"') {
                    if (modeStack.peek() == Mode.STRING) {
                        tokens.add(Token.StringEnd)
                        modeStack.pop()
                        continue
                    }

                    if (modeStack.peek() == Mode.VARIABLE) {
                        tokens.add(Token.Character(char))
                        continue
                    }

                    tokens.add(Token.StringStart)
                    modeStack.push(Mode.STRING)
                    continue
                }

                tokens.add(Token.Character(char))
                continue
            }

            if (tokens.isNotEmpty() && !tokens.last().terminatingToken) {
                // Ensure that all modes eventually close, just as blocks close
                while (modeStack.isNotEmpty()) {
                    val mode = modeStack.pop()!!
                    when (mode) {
                        Mode.STRING -> tokens.add(Token.StringEnd)
                        Mode.INTERPOLATION -> tokens.add(Token.InterpolationEnd)
                        Mode.VARIABLE -> tokens.add(Token.VariableEnd)
                    }
                }

                tokens.add(Token.EndStatement)
            }
        }

        for (i in 1..openBlocks)
            tokens.add(Token.BlockEnd)

        return tokens
    }

    private fun lookahead(chars: CharArray, current: Int, ahead: Int): String {
        val builder = StringBuilder()
        for (i in current .. (current + ahead - 1).coerceAtMost(chars.size - 1))
            builder.append(chars[i])

        return builder.toString()
    }

    private fun getIndentationLevel(content: String, size: Int): Int {
        val replaced = content.replace('\t'.toString(), "    ")

        var spaces = 0
        var level = 0
        for (char in replaced.toCharArray()) {
            if (char == ' ') {
                spaces++
                if (spaces % size == 0) {
                    level++
                }
            } else return level
        }

        return 0
    }

    private fun firstTokenOfLine(tokens: List<Token>): Boolean =
        tokens.isEmpty() || tokens.last().terminatingToken
}