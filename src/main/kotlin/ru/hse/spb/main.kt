package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

fun main(args: Array<String>) {
    val file = Files.lines(Paths.get(args[0])).collect(Collectors.joining("\n"))
    val lexer = ExpLexer(CharStreams.fromString(file))
    val parser = ExpParser(BufferedTokenStream(lexer))
    val visitor = ExpFunVisitor(System.out)
    visitor.visitFile(parser.file())
}