package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import java.io.File

fun main(args: Array<String>) {
    val file = File(args[0]).readText()
    val lexer = ExpLexer(CharStreams.fromString(file))
    val parser = ExpParser(BufferedTokenStream(lexer))
    val visitor = ExpFunVisitor(System.out)
    visitor.visitFile(parser.file())
}