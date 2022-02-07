package org.csc.kotlin2021.mastermind.matching

import org.csc.kotlin2021.mastermind.getAns
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class SequenceMatchingTest {

    companion object {
        @JvmStatic
        fun sequences() = listOf(
            Arguments.of("ACEB", "BCDF", 1, 1),
            Arguments.of("AAAA", "AAAA", 4, 0),
            Arguments.of("AABB", "BBAA", 0, 4),
            Arguments.of("AAAB", "AAAA", 3, 0),
            Arguments.of("ABCD", "AAAA", 1, 0),
            Arguments.of("ABCD", "ABCD", 4, 0),
            Arguments.of("ABCD", "EEEE", 0, 0),
            Arguments.of("AAAA", "BBAA", 2, 0),
            Arguments.of("BAAB", "BBAB", 3, 0),
            Arguments.of("ABCDEFGH", "HGFEDCBA", 0, 8),
            Arguments.of("ABCDEFGH", "ABCDEFGH", 8, 0)
        )
    }

    @ParameterizedTest
    @MethodSource("sequences")
    fun testSequenceMatching(initial: String, actual: String, expectedFullMatch: Int, expectedPartMatch: Int) {
        val ans = getAns(initial, actual)
        val actualFullMatch = ans.first
        val actualPartMatch = ans.second
        Assertions.assertEquals(
            expectedFullMatch, actualFullMatch, "Full matches don't equal! " +
                    "Actual full match count = $actualFullMatch, expected full match count = $expectedFullMatch"
        )
        Assertions.assertEquals(
            expectedPartMatch, actualPartMatch, "Part matches don't equal! " +
                    "Part full part count = $actualPartMatch, expected part match count = $expectedPartMatch"
        )
    }
}
