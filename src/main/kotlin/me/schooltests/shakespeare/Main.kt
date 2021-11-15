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

package me.schooltests.shakespeare

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("(!) Shakespeare Compiler (!)")
        println("Please use a valid entry-point command")
        println("- compile | index -")
        return
    }

    val keyedArguments = mutableMapOf<String, String>(); var last = ""
    for (arg in args.copyOfRange(1, args.size)) {
        if (arg.startsWith("--")) {
            val key = arg.substring(2)
            last = key
        }

        keyedArguments[last] = ((keyedArguments[last] ?: "") + " " + arg).trimStart()
    }

    if (args[0].equals("index", ignoreCase = true)) {
        return
    }

    if (args[0].equals("compile", ignoreCase = true)) {
        return
    }
}