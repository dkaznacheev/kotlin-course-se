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
            "fun fib(x) {println(x)} if (1) {fun fib(x) {println(x + 1, 0)}  fib(1)} println(fib(1))"
    ))
    val parser = ExpParser(BufferedTokenStream(lexer))
    val visitor = ExpFunVisitor(System.out)
    visitor.visitFile(parser.file())
}