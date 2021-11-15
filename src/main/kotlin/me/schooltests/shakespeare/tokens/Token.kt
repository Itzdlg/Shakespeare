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
