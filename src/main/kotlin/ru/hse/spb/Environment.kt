package ru.hse.spb

import ru.hse.spb.parser.ExpParser

open class NoVariableException: InterpreterException()
open class NoFunctionException: InterpreterException()

class Environment(val parent: Environment?) {
    var result: Int? = null
    private val variables: MutableMap<String, Int?> = mutableMapOf()
    private val functions: MutableMap<Pair<String, Int>, Pair<List<String>, ExpParser.BlockContext?>> =
            mutableMapOf()

    fun getVariable(name: String): Int? {
        if (variables.containsKey(name))
            return variables[name]
        return parent?.getVariable(name) ?: throw NoVariableException()
    }

    fun setVariable(name: String, value: Int) {
        if (variables.containsKey(name))
            variables[name] = value
        else
            parent?.setVariable(name, value) ?: throw NoVariableException()
    }

    fun addVariable(name: String) {
        variables[name] = null
    }

    fun getFunction(name: String, argc: Int): Pair<List<String>, ExpParser.BlockContext?> {
        return functions[Pair(name, argc)]
                ?: parent?.getFunction(name, argc)
                ?: throw NoFunctionException()
    }

    fun addFunction(name: String, args: List<String>, body: ExpParser.BlockContext?) {
        functions[Pair(name, args.size)] = Pair(args, body)
    }

    fun printVariables() {
        println("variables:")
        variables.forEach{name, value -> println("$name = $value")}
        parent?.printVariables()
    }

    fun printFunctions() {
        println("functions:")
        functions.forEach{func, _ -> println(func.first + "(" + func.second + " args)")}
        parent?.printFunctions()
    }
}