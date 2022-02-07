package org.csc.kotlin2021.mastermind

data class SettingsForTest(
    override val countTries: Int, override val canRepeated: Boolean, override val maxLetter: Char,
    override val countLetters: Int, override val needHighScoreTable: Boolean,
    override val secret: String, override val player: Player
) : Settings
