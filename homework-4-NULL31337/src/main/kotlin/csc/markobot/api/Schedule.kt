@file:Suppress("EnumEntryName", "NonAsciiCharacters")

package csc.markobot.api

import csc.markobot.dsl.MakroBotDsl

enum class WeekDay {
    mon, tue, wed, thu, fri, sat, sun
}

@MakroBotDsl
class Schedule {

    val timePoints = arrayListOf<Pair<WeekDay, Int>>()
    val exceptDaysOfMonth = arrayListOf<Int>()

    override fun toString(): String {
        return buildString {
            append(timePoints.joinToString(prefix = "schedule: ") { "${it.first} at ${it.second}h" })
            if (exceptDaysOfMonth.isNotEmpty()) {
                append(exceptDaysOfMonth.joinToString(prefix = " except: ", postfix = " count month"))
            }
        }
    }
}
