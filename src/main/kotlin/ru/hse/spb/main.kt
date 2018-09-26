package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser

fun getGreeting(): String {
    val words = mutableListOf<String>()
    words.add("Hello,")
    words.add("world!")

    return words.joinToString(separator = " ")
}

fun main(args: Array<String>) {
    val lexer = ExpLexer(CharStreams.fromString(
            "fun fib(x) {" +
                    "if (x == 0) {return 1}" +
                    "return fib(x - 1)" +
                    "}" +
                    "print(fib(0))"
    ))
    val parser = ExpParser(BufferedTokenStream(lexer))
    val visitor = ExpFunVisitor()
    visitor.visitFile(parser.file())
}