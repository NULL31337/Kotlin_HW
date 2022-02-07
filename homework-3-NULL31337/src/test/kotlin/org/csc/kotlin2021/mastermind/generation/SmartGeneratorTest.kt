package org.csc.kotlin2021.mastermind.generation

import org.csc.kotlin2021.mastermind.SettingsForTest
import org.csc.kotlin2021.mastermind.TestPlayer
import org.csc.kotlin2021.mastermind.playMastermind
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class SmartGeneratorTest {

    @Test
    @RepeatedTest(100)
    fun `smart generator 4 letters 100 times`() {
        val setting = SettingsForTest(1, true, 'H', 4, false, "", TestPlayer(listOf()))
        assertDoesNotThrow { setting.checkGuessCorrect(setting.generateSecret()) }
    }

    @Test
    @RepeatedTest(100)
    fun `smart generator 8 letters 100 times`() {
        val setting = SettingsForTest(1, true, 'H', 8, false, "", TestPlayer(listOf()))
        assertDoesNotThrow { setting.checkGuessCorrect(setting.generateSecret()) }
    }

    @Test
    @RepeatedTest(100)
    fun `smart generator 20 letters Z maxLetter 100 times`() {
        val setting = SettingsForTest(1, true, 'Z', 20, false, "", TestPlayer(listOf()))
        assertDoesNotThrow { setting.checkGuessCorrect(setting.generateSecret()) }
    }

    @Test
    fun `smart all letters use`() {
        val setting = SettingsForTest(1, true, 'Z', 20, false, "", TestPlayer(listOf()))
        val usedSet = HashSet<Char>()
        for (i in 1..1000) {
            val secret = setting.generateSecret()
            secret.forEach { usedSet.add(it) }
            assertDoesNotThrow { setting.checkGuessCorrect(secret) }
        }
        assertTrue(usedSet.size == 26)
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("settingsDoesNotThrow")
    fun correctnessGame(SettingsForTest: SettingsForTest) {
        assertDoesNotThrow {
            playMastermind(SettingsForTest)
        }
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("settingsThrowsIllegalArgumentException")
    fun incorrectnessGame(SettingsForTest: SettingsForTest) {
        assertThrows<IllegalArgumentException> {
            playMastermind(SettingsForTest)
        }
    }

    companion object {
        @JvmStatic
        fun settingsDoesNotThrow() = listOf(
            Arguments.of(
                Named.of(
                    "Test correct",
                    SettingsForTest(
                        100, true,
                        'H', 4, false, "ABAD", TestPlayer(listOf("ABAD"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test correct 2 guess",
                    SettingsForTest(
                        100, true,
                        'H', 4, false, "ABAD", TestPlayer(listOf("AAAA", "ABAD"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test correct more than 1 guess",
                    SettingsForTest(
                        100, true,
                        'H', 4, false, "ABAD",
                        TestPlayer(listOf("ABCE", "AECD", "ABFD", "AECD", "ABED", "ABAD"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test correct another maxLetter",
                    SettingsForTest(
                        100, true,
                        'Z', 4, false, "ZZZZ", TestPlayer(listOf("ZZZZ"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test correct only A",
                    SettingsForTest(
                        100, true,
                        'A', 4, false, "AAAA", TestPlayer(listOf("AAAA"))
                    )
                )
            )
        )

        @JvmStatic
        fun settingsThrowsIllegalArgumentException() = listOf(
            Arguments.of(
                Named.of(
                    "Test guess longer",
                    SettingsForTest(
                        100, true,
                        'H', 4, false, "ABCD", TestPlayer(listOf("ABCAE"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test incorrect guess maxLetter",
                    SettingsForTest(
                        100, true,
                        'H', 4, false, "ABAD", TestPlayer(listOf("ABAL"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test guess shorter",
                    SettingsForTest(
                        100, true,
                        'H', 4, false, "ABCD", TestPlayer(listOf("ABA"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test guess longer with repeated",
                    SettingsForTest(
                        100, true,
                        'H', 3, false, "BCD", TestPlayer(listOf("AAAA"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test guess shorter with repeated",
                    SettingsForTest(
                        100, true,
                        'H', 5, false, "ABCAD", TestPlayer(listOf("AAAA"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test guess longer more than 1 guess",
                    SettingsForTest(
                        100, true,
                        'H', 4, false, "ABAD",
                        TestPlayer(listOf("ABAE", "AECD", "ABFD", "AECD", "ABED", "ABCDE"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test incorrect guess maxLetter  more than 1 guess",
                    SettingsForTest(
                        100, true,
                        'H', 4, false, "ABAD",
                        TestPlayer(listOf("ABAE", "AECD", "ABFD", "AECD", "ABED", "ABCL"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test guess shorter more than 1 guess",
                    SettingsForTest(
                        100, true,
                        'H', 4, false, "ABAD",
                        TestPlayer(listOf("ABAE", "AECD", "ABFD", "AECD", "ABED", "ABC"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test guess maxLetter more than setting maxLetter",
                    SettingsForTest(
                        100, true,
                        'B', 4, false, "AABB", TestPlayer(listOf("ABAC"))
                    )
                )
            )
        )
    }
}
