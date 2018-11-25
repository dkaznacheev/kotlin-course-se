package ru.hse.spb

open class NoVariableException: InterpreterException()
open class NoFunctionException: InterpreterException()

class Environment(val parent: Environment?) {
    var result: Int? = null
    private val variables: MutableMap<String, Int?> = mutableMapOf()
    private val functions: MutableMap<String, Pair<List<String>, ExpParser.BlockContext?>> =
            mutableMapOf()

    fun getVariable(name: String): Int? {
        if (name in variables)
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

    fun getFunction(name: String): Pair<List<String>, ExpParser.BlockContext?> {
        return functions[name]
                ?: parent?.getFunction(name)
                ?: throw NoFunctionException()
    }

    fun addFunction(name: String, args: List<String>, body: ExpParser.BlockContext?) {
        functions[name] = Pair(args, body)
    }
}