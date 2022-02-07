package org.csc.kotlin2021.mastermind

import java.util.*

fun playMastermind(settings: Settings) {
    var cnt = settings.countTries
    while (cnt > 0) {
        val guess = settings.player.guess()
        cnt =
            if (settings.checkGuessCorrect(guess)) {
                val bonusForTheNumberOfSelectedAttempts = 31337 - 10 * settings.countTries
                val bonusForTheNumberOfRemainingAttempts = (settings.countTries - cnt) * settings.countLetters
                val repetitionBonus = if (settings.canRepeated) 10000 else 0
                -(bonusForTheNumberOfSelectedAttempts + bonusForTheNumberOfRemainingAttempts + repetitionBonus)
            } else {
                cnt - 1
            }
        val ans = getAns(settings.secret, guess)
        settings.player.receiveEvaluation(cnt, ans.first, ans.second, settings.needHighScoreTable)
    }
}

interface Settings {
    val countTries: Int
    val canRepeated: Boolean
    val maxLetter: Char
    val countLetters: Int
    val needHighScoreTable: Boolean
    val secret: String
    val player: Player

    fun checkGuessCorrect(guess: String): Boolean {
        if (guess.length != countLetters || guess.any { it !in 'A'..maxLetter } ||
            (!canRepeated && guess.toSet().size != countLetters)) {
            player.incorrectInput(guess, countLetters, canRepeated, maxLetter)
        }
        return guess == secret
    }

    fun generateSecret(): String {
        val letters = ('A'..maxLetter)
        return if (!canRepeated) {
            letters.shuffled().joinToString("").take(countLetters)
        } else {
            buildString {
                for (i in 1..countLetters) {
                    append(letters.random())
                }
            }
        }
    }
}

class SettingsClassic : Settings {
    override val countTries: Int = 4
    override val canRepeated: Boolean = false
    override val maxLetter: Char = 'H'
    override val countLetters: Int = 4
    override val needHighScoreTable: Boolean = false
    override val secret: String = generateSecret()
    override val player: Player = RealPlayer()
}

class SettingsScanner(private val scanner: Scanner = Scanner(System.`in`)) : Settings {
    override val countTries: Int = readNumber("Enter the number of attempts") { it < 0 }
    override val canRepeated: Boolean = readTrueOrFalse("Can letters be repeated?(y/n)")
    override val maxLetter: Char = readString(
        "Enter max letter",
        "Oops it's not a letter or it's not in the range from 'A' to 'Z'"
    ) { !(it.length == 1 && 'A' <= it[0] && it[0] <= 'Z') }[0]
    override val countLetters: Int =
        readNumber("Enter the number of letters") { it <= 0 || (!canRepeated && it > maxLetter - 'A' + 1) }
    override val needHighScoreTable: Boolean = readTrueOrFalse("Do you need a high score table?(y/n)")
    override val secret: String = generateSecret()
    override val player: Player = RealPlayer()

    private fun readString(message: String, wrongMessage: String, func: (c: String) -> Boolean): String {
        var c: String
        while (true) {
            println(message)
            c = scanner.next()
            if (func(c)) {
                println(wrongMessage)
            } else {
                break
            }
        }
        return c
    }

    private fun readTrueOrFalse(message: String): Boolean {
        return readString(message, "Oops it's not y or n") { c: String -> c != "y" && c != "n" } == "y"
    }

    private fun readNumber(message: String, func: (c: Int) -> Boolean): Int {
        var c: Int
        while (true) {
            println(message)
            c = try {
                scanner.nextInt()
            } catch (e: InputMismatchException) {
                scanner.next()
                println("Oops this is not a number")
                -1
            }
            if (func(c)) {
                println("Oops this is a lot of letters for a word without repetitions with max letter $maxLetter")
            } else {
                break
            }
        }
        return c
    }
}

fun getAns(secret: String, guess: String): Pair<Int, Int> {
    val ans = secret.zip(guess).filter { it.first != it.second }.unzip()
    val positions = secret.length - ans.first.size
    var letters = 0
    val cntSecret = IntArray(26)
    ans.first.forEach {
        cntSecret[it - 'A']++
    }
    ans.second.forEach {
        if (cntSecret[it - 'A'] > 0) {
            letters++
            cntSecret[it - 'A']--
        }
    }
    return positions to letters
}
