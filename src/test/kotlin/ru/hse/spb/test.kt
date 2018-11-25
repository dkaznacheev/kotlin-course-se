package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class TestSource {

    private fun testProgram(program: String): String {
        val lexer = ExpLexer(CharStreams.fromString(program))
        val parser = ExpParser(BufferedTokenStream(lexer))
        val testStream = ByteArrayOutputStream()
        val testPrintStream = PrintStream(testStream)
        val visitor = ExpFunVisitor(testPrintStream)
        visitor.visitFile(parser.file())
        return testStream.toString()
    }

    @Test
    fun testParser() {
        val lexer = ExpLexer(CharStreams.fromString(
                "var x = 1\nprintln(x + 1)"
        ))
        val parser = ExpParser(BufferedTokenStream(lexer))
        assertEquals(2, parser.file().body.children.size)
    }

    @Test
    fun testAssignment01() {
        assertEquals("1\n", testProgram(
                "var x = 1\nprintln(x)"))
    }

    @Test
    fun testAssignment02() {
        assertEquals("1\n", testProgram(
                "var x\nx = 1\nprintln(x)"))
    }

    @Test
    fun testIf01() {
        assertEquals("1\n", testProgram(
                "if(1){println(1)}"))
    }

    @Test
    fun testIf02() {
        assertEquals("", testProgram(
                "if(0){println(1)}"))
    }

    @Test
    fun testIf03() {
        assertEquals("1\n", testProgram(
                "if(0){println(0)} else {println(1)}"))
    }

    @Test
    fun testBinOp() {
        assertEquals("239\n", testProgram(
                "println(239 * (1 + 1 * (3 - 2) - 2 / 2))"))
    }

    @Test
    fun testFunctionCall() {
        assertEquals("1\n", testProgram(
                "fun f(x) {return x - 1} println(f(2))"))
    }

    @Test
    fun testOuterReturn() {
        assertEquals("1\n", testProgram(
                "fun f(x) {if (1) {return x} else {return x}} println(f(1))"))
    }

    @Test
    fun testWhile() {
        assertEquals("3\n2\n1\n", testProgram(
                "var i = 3 while (i > 0) {println(i) i = i - 1}"))
    }

}
