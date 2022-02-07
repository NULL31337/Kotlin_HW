package org.csc.kotlin2021.mastermind.generation

import org.csc.kotlin2021.mastermind.SettingsForTest
import org.csc.kotlin2021.mastermind.TestPlayer
import org.csc.kotlin2021.mastermind.playMastermind
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class BaseGeneratorTest {

    @Test
    @RepeatedTest(100)
    fun `base generator 4 letters 100 times`() {
        val setting = SettingsForTest(1, false, 'H', 4, false, "", TestPlayer(listOf()))
        assertDoesNotThrow { setting.checkGuessCorrect(setting.generateSecret()) }
    }

    @Test
    @RepeatedTest(100)
    fun `base generator 8 letters 100 times`() {
        val setting = SettingsForTest(1, false, 'H', 8, false, "", TestPlayer(listOf()))
        assertDoesNotThrow { setting.checkGuessCorrect(setting.generateSecret()) }
    }

    @Test
    @RepeatedTest(100)
    fun `base generator 20 letters Z maxLetter 100 times`() {
        val setting = SettingsForTest(1, false, 'Z', 20, false, "", TestPlayer(listOf()))
        assertDoesNotThrow { setting.checkGuessCorrect(setting.generateSecret()) }
    }

    @Test
    @RepeatedTest(100)
    fun `base generator 4 letters maxLetter Z 100 times`() {
        val setting = SettingsForTest(1, false, 'Z', 4, false, "", TestPlayer(listOf()))
        assertDoesNotThrow { setting.checkGuessCorrect(setting.generateSecret()) }
    }

    @Test
    fun `base all letters use`() {
        val setting = SettingsForTest(1, false, 'Z', 20, false, "", TestPlayer(listOf()))
        val usedSet = HashSet<Char>()
        for (i in 1..1000) {
            val secret = setting.generateSecret()
            secret.forEach { usedSet.add(it) }
            assertDoesNotThrow { setting.checkGuessCorrect(secret) }
        }
        assertTrue(usedSet.size == 26)
    }

    companion object {
        @JvmStatic
        fun settingsDoesNotThrow() = listOf(
            Arguments.of(
                Named.of(
                    "Test correctness",
                    SettingsForTest(
                        100, false, 'H', 4,
                        false, "ABCD", TestPlayer(listOf("ABCD"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test correct more than 1 guess",
                    SettingsForTest(
                        100, false,
                        'H', 4, false, "ABCD",
                        TestPlayer(listOf("ABCE", "AECD", "ABFD", "AECD", "ABED", "ABCD"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test another maxLetter",
                    SettingsForTest(
                        100, false,
                        'Z', 4, false, "ABCZ", TestPlayer(listOf("ABCZ"))
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
                        100, false,
                        'H', 4, false, "ABCD", TestPlayer(listOf("ABCDE"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test incorrect letter",
                    SettingsForTest(
                        100, false,
                        'H', 4, false, "ABCD", TestPlayer(listOf("ABCL"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test guess shorter",
                    SettingsForTest(
                        100, false,
                        'H', 4, false, "ABCD", TestPlayer(listOf("ABC"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test repeating letters",
                    SettingsForTest(
                        100, false,
                        'H', 4, false, "ABCD", TestPlayer(listOf("AAAA"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test repeating letters longer",
                    SettingsForTest(
                        100, false,
                        'H', 3, false, "ABCD", TestPlayer(listOf("AAAA"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test repeating letters shorter",
                    SettingsForTest(
                        100, false,
                        'H', 5, false, "ABCD", TestPlayer(listOf("AAAA"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test guess longer more than 1 guess",
                    SettingsForTest(
                        100, false,
                        'H', 4, false, "ABCD",
                        TestPlayer(listOf("ABCE", "AECD", "ABFD", "AECD", "ABED", "ABCDE"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test incorrect letter more than 1 guess",
                    SettingsForTest(
                        100, false,
                        'H', 4, false, "ABCD",
                        TestPlayer(listOf("ABCE", "AECD", "ABFD", "AECD", "ABED", "ABCL"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test guess shorter more than 1 guess",
                    SettingsForTest(
                        100, false,
                        'H', 4, false, "ABCD",
                        TestPlayer(listOf("ABCE", "AECD", "ABFD", "AECD", "ABED", "ABC"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test repeating letters more than 1 guess",
                    SettingsForTest(
                        100, false,
                        'H', 4, false, "ABCD",
                        TestPlayer(
                            listOf(
                                "ABCE",
                                "AECD",
                                "ABFD",
                                "AECD",
                                "ABED",
                                "AAAA"
                            )
                        )
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test incorrect guess",
                    SettingsForTest(
                        100, false,
                        'D', 4, false, "ABCD", TestPlayer(listOf("ADFE"))
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Test longer guess another maxLetter",
                    SettingsForTest(
                        100, false,
                        'Z', 4, false, "ABCZ", TestPlayer(listOf("ABZCS"))
                    )
                ),
            )
        )
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
}
