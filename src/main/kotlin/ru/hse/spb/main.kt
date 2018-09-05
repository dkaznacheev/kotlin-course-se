package ru.hse.spb

import java.util.*

fun formattedName(lettersNumber: Int, template: String): String {
    val letters: List<Char> = listOf('a'..'z')
            .flatten()
            .take(lettersNumber)
            .minus(template.toList())

    val name = template.toCharArray()

    val n = template.length
    var wildCards = 0

    for (i in 0 until (n + 1) / 2) {
        if (name[i] != name[n - i - 1]) {
            when {
                name[i] == '?' -> name[i] = name[n - i - 1]
                name[n - i - 1] == '?' -> name[n - i - 1] = name[i]
                else -> return "IMPOSSIBLE"
            }
        } else if (name[i] == '?')
            wildCards++
    }

    if (wildCards < letters.size)
        return "IMPOSSIBLE"

    var offset = letters.size - wildCards

    for (i in 0 until (n + 1) / 2) {
        if (name[i] == '?') {
            val letter = if (offset < 0) 'a' else letters[offset]
            name[i] = letter
            name[n - i - 1] = letter
            offset++
        }
    }

    return name.joinToString("")
}

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    val lettersNumber = scanner.nextInt()
    val template = scanner.next()

    println(formattedName(lettersNumber, template))
}
