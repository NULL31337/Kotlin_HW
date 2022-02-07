package org.csc.kotlin2021.mastermind

import java.io.File
import java.io.FileNotFoundException
import java.util.*

interface Player {
    fun guess(): String
    fun receiveEvaluation(score: Int, positions: Int, letters: Int, highScoreTable: Boolean)
    fun incorrectInput(guess: String, countLetters: Int, differentLetters: Boolean, maxLetter: Char) {}
    val name: String
}

abstract class MainPlayer : Player {
    override fun receiveEvaluation(score: Int, positions: Int, letters: Int, highScoreTable: Boolean) {
        if (score >= 0) {
            println("Positions: $positions; letters: $letters.")
        } else {
            println("You are correct!")
            if (highScoreTable) {
                val highScore = mutableListOf<Pair<Int, String>>()
                highScore.add(-score to name)
                try {
                    File("Records.txt").forEachLine {
                        val tmp = it.split(" ")
                        highScore.add(tmp.first().toInt() to tmp.last())
                    }
                } catch (e: FileNotFoundException) {
                    File("Records.txt").createNewFile()
                }
                val sb = buildString {
                    highScore.sortedByDescending { it.first }
                        .forEach { append("${it.first} ${it.second}\n") }
                }
                println(
                    """
                        |Your score: ${-score}
                        |HIGH SCORE TABLE:
                        |$sb
                    """.trimMargin()
                )
                File("Records.txt").writeText(sb)
            }
        }
        if (score == 0) {
            println("You lose AHAHHAHAHAHHAHAHHA!")
        }
    }

    override fun incorrectInput(guess: String, countLetters: Int, differentLetters: Boolean, maxLetter: Char) {
        print("Incorrect input: $guess. It should consist of $countLetters letters from A to $maxLetter. ")
        if (differentLetters) {
            println("Repetitions allowed")
        } else {
            println("Repetitions prohibited")
        }
        throw IllegalArgumentException("Incorrect input")
    }
}

class RealPlayer : MainPlayer() {
    private val scanner = Scanner(System.`in`)
    override val name: String = readName()
    override fun guess(): String {
        print("Your guess: ")
        return scanner.next()
    }

    private fun readName(): String {
        var c = ""
        while (c == "") {
            println("Enter your name")
            c = scanner.next()
        }
        return c
    }
}

class TestPlayer(private var guessList: List<String>) : MainPlayer() {
    override val name = "Test"
    private var currentGuess = 0
    override fun guess(): String {
        return guessList[currentGuess++]
    }
}
