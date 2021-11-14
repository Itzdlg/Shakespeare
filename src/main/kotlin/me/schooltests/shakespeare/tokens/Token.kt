package me.schooltests.shakespeare.tokens

sealed class Token(val terminatingToken: Boolean = false) {
    class Character(val character: Char) : Token() {
        fun TokenQueue.requireAndConsumeCharacters(chars: CharSequence) {
            for (char in chars)
                requireAndConsume(Character(char))
        }
    }
    class Digit(val character: Char) : Token()
    object Space : Token()

    object BlockStart : Token(terminatingToken = true)
    object BlockEnd : Token()
    
    object LeftParenthesis : Token()
    object RightParenthesis : Token()
    object LeftBracket : Token()
    object RightBracket : Token()
    object LeftArrow : Token()
    object RightArrow : Token()

    object StringStart : Token()
    object StringEnd : Token()
    object InterpolationStart : Token()
    object InterpolationEnd : Token()
    object VariableStart : Token()
    object VariableEnd : Token()

    object Period : Token()
    object Comma : Token()

    object EndStatement : Token(terminatingToken = true)
}
