package ru.hse.spb

import java.util.*

private fun isQuestion(c: Char) = c == '?'

fun formattedName(lettersNumber: Int, template: String): String? {
    val name = template.toCharArray()

    val n = template.length
    var questionMarks = 0

    for (l in 0 until (n + 1) / 2) {
        val r = n - l - 1
        if (name[l] != name[r]) {
            when {
                isQuestion(name[l]) -> name[l] = name[r]
                isQuestion(name[r]) -> name[r] = name[l]
                else -> return null
            }
        } else if (isQuestion(name[l]))
            questionMarks++
    }

    val letters: List<Char> = ('a'..'z')
            .take(lettersNumber)
            .minus(template.toList())

    if (questionMarks < letters.size)
        return null

    var offset = letters.size - questionMarks

    for (l in 0 until (n + 1) / 2) {
        val r = n - l - 1
        if (!isQuestion(name[l]))
            continue
        val letter = if (offset < 0) 'a' else letters[offset]
        name[l] = letter
        name[r] = letter
        offset++
    }

    return String(name)
}

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    val lettersNumber = scanner.nextInt()
    val template = scanner.next()

    println(formattedName(lettersNumber, template) ?: "IMPOSSIBLE")
}
