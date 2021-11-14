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