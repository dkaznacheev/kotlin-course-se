package ru.hse.spb

import ru.hse.spb.parser.ExpBaseVisitor
import ru.hse.spb.parser.ExpParser
import java.io.OutputStream
import java.io.PrintStream

open class UnsupportedOperatorException:InterpreterException()
open class EvaluationException:InterpreterException()
open class NullDivisionException:InterpreterException()
open class IncorrectArgsException:InterpreterException()

class ExpFunVisitor(val outputStream: OutputStream): ExpBaseVisitor<Int?>() {
    private var env: Environment = Environment(null)
    private val printStream: PrintStream = PrintStream(outputStream)

    override fun visitFile(ctx: ExpParser.FileContext): Int? {
        env.addFunction("println", listOf(), null)
        return visit(ctx.block())
    }

    override fun visitBlock(ctx: ExpParser.BlockContext): Int? {
        for (stmt in ctx.children) {
            visit(stmt)
            if (env.result != null)
                break
        }
        return env.result
    }

    override fun visitStatement(ctx: ExpParser.StatementContext): Int? {
        ctx.children.forEach {visit(it)}
        return null
    }

    override fun visitAssignment(ctx: ExpParser.AssignmentContext): Int? {
        env.setVariable(ctx.name.text, visit(ctx.value)!!)
        return null
    }

    override fun visitVariable(ctx: ExpParser.VariableContext): Int? {
        val name = ctx.name.text
        env.addVariable(name)
        if (ctx.value != null)
            env.setVariable(name, visit(ctx.value)!!)
        return null
    }

    override fun visitFunction(ctx: ExpParser.FunctionContext): Int? {
        val name = ctx.name.text
        val params: List<String> = getParameterNames(ctx.params)
        env.addFunction(name, params, ctx.block())
        return null
    }

    private fun getParameterNames(params: ExpParser.Parameter_namesContext?): List<String> {
        if (params == null)
            return listOf()
        return params.identifier().map { it.text }
    }

    override fun visitExpression(ctx: ExpParser.ExpressionContext): Int? {
        if (ctx.children.first().text == "(")
            return visit(ctx.children[1])

        if (ctx.left != null && ctx.right != null && ctx.op != null) {
            val op = ctx.op.text
            val left: Int = visit(ctx.left) ?: throw EvaluationException()

            if (op == "&&" && left == 0)
                return 0

            if (op == "||" && left != 0)
                return 1

            val right: Int = visit(ctx.right) ?: throw EvaluationException()

            return when(op) {
                "*" -> left * right
                "/" -> if (right != 0) left / right else throw NullDivisionException()
                "%" -> if (right != 0) left % right else throw NullDivisionException()
                "+" -> left + right
                "-" -> left - right
                "<=" -> if (left <= right) 1 else 0
                "<" -> if (left < right) 1 else 0
                ">=" -> if (left >= right) 1 else 0
                ">" -> if (left > right) 1 else 0
                "==" -> if (left == right) 1 else 0
                "!=" -> if (left != right) 1 else 0
                "&&" -> if (left != 0 && right != 0) 1 else 0
                "||" -> if (left != 0 || right != 0) 1 else 0
                else -> throw UnsupportedOperatorException()
            }
        }
        return visit(ctx.children.first())
    }

    override fun visitIdentifier(ctx: ExpParser.IdentifierContext): Int? {
        return env.getVariable(ctx.text)
    }

    override fun visitLiteral(ctx: ExpParser.LiteralContext): Int? {
        return ctx.text.toInt()
    }

    override fun visitFunction_call(ctx: ExpParser.Function_callContext): Int? {
        val name = ctx.name.text

        if (name == "println") {
            printStream.println(
                    ctx.args.expression()
                            .asSequence()
                            .map { visit(it) }
                            .joinToString(" ")
            )
            return 0
        }

        val argc = ctx.args.expression().size
        val foundFunction = env.getFunction(name)
        if (argc != foundFunction.first.size)
            throw IncorrectArgsException()

        val args = foundFunction.first
        val argValues: List<Int> =
                ctx.args.expression().map { visit(it) ?: throw EvaluationException() }

        val innerEnv = Environment(env)
        for (i in 0 until argc) {
            innerEnv.addVariable(args[i])
            innerEnv.setVariable(args[i], argValues[i])
        }
        env = innerEnv
        val result = visitBlock(foundFunction.second!!)
        env = env.parent!!

        return result ?: 0
    }

    override fun visitT_return(ctx: ExpParser.T_returnContext): Int? {
        val res = visit(ctx.value)
        env.result = res
        return null
    }

    override fun visitT_if(ctx: ExpParser.T_ifContext): Int? {
        val cond = visit(ctx.condition)
        val block: ExpParser.BlockContext? =
                if (cond != 0) ctx.block(0) else ctx.block(1)
        if (block != null) {
            env = Environment(env)
            visitBlock(block)
            val blockResult = env.result
            env = env.parent!!
            env.result = blockResult
        }
        return null
    }

    override fun visitT_while(ctx: ExpParser.T_whileContext): Int? {
        while (visit(ctx.condition) != 0) {
            env = Environment(env)
            visitBlock(ctx.block())
            val blockResult = env.result
            env = env.parent!!
            env.result = blockResult
            if (blockResult != null)
                return null
        }
        return null
    }
}