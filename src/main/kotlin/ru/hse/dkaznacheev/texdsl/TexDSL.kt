package ru.hse.dkaznacheev.texdsl

import java.io.PrintStream


@DslMarker
annotation class TexMarker

fun indentOf(length: Int) : String {
    return "  ".repeat(length)
}

interface Element {
    fun render(out: PrintStream, indent: Int)
}

class TextElement(private val text: String) : Element {
    override fun render(out: PrintStream, indent: Int) {
        out.println(indentOf(indent) + text)
    }
}

class MathElement(private val text: String) : Element {
    override fun render(out: PrintStream, indent: Int) {
        out.println(indentOf(indent) + "$$text$")
    }
}


@TexMarker
abstract class Block(
        protected val name: String,
        protected vararg val options: String) : Element {
    protected val children = mutableListOf<Element>()

    override fun render(out: PrintStream, indent: Int) {
        out.print(indentOf(indent) + "\\begin{$name}")
        if (options.isNotEmpty())
            out.print("[" + options.joinToString(",") + "]")
        out.println()
        children.forEach { it.render(out, indent + 1) }
        out.println(indentOf(indent) + "\\end{$name}")
    }

    fun <T : Block> initElement(element: T, init: T.() -> Unit): T {
        element.init()
        children.add(element)
        return element
    }

    fun enumerate(init: Enumerate.() -> Unit) = initElement(Enumerate(), init)
    fun itemize(init: Itemize.() -> Unit) = initElement(Itemize(), init)
}

abstract class OpenCloseBlock(
        name: String,
        vararg options: String) : Block(name, *options) {

    override fun render(out: PrintStream, indent: Int) {
        out.print(indentOf(indent) + "\\begin{$name}")
        if (options.isNotEmpty())
            out.print("[" + options.joinToString(",") + "]")
        out.println()
        children.forEach { it.render(out, indent + 1) }
        out.println(indentOf(indent) + "\\end{$name}")
    }
}

abstract class TextBlock(name: String, vararg options: String) : OpenCloseBlock(name, *options) {
    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }
}

class Document : TextBlock("document") {
    private val header = mutableListOf<Element>()

    fun documentClass(content: String, vararg options: String) {
        header.add(DocumentClass(content, *options))
    }

    fun usePackage(content: String, vararg options: String) {
        header.add(UsePackage(content, *options))
    }

    override fun render(out: PrintStream, indent: Int) {
        header.forEach {it.render(out, indent)}
        super.render(out, indent)
    }
}

class Item: TextBlock("item")

abstract class ItemHolder(name: String, vararg options: String) : OpenCloseBlock (name, *options) {
    fun item(init: Item.() -> Unit): Item {
        return initElement(Item(), init)
    }
}

class Enumerate(vararg options: String): ItemHolder("enumerate", *options)

class Itemize(vararg options: String): ItemHolder("itemize", *options)

abstract class Tag (
        private val name: String,
        private val content: String,
        private vararg val options: String) : Element {
    override fun render(out: PrintStream, indent: Int) {
        out.print(indentOf(indent))
        out.print("\\$name")
        if (options.isNotEmpty()) {
            out.print("[")
            out.print(options.joinToString(","))
            out.print("]")
        }
        out.println("{$content}")
    }
}

class DocumentClass(content: String,
                    vararg options: String) : Tag("documentclass", content, *options)

class UsePackage(content: String,
                    vararg options: String) : Tag("usepackage", content, *options)

fun document(init: Document.() -> Unit): Document {
    val document = Document()
    document.init()
    return document
}

fun main(args: Array<String>) {
    document {
        usePackage("babel", "asd", "qwe")
        documentClass("paper")
        enumerate { item { +"hi" } }
    }.render(System.out, 0)
}
