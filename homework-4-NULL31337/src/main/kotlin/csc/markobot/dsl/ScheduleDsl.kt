@file:Suppress("EnumEntryName", "NonAsciiCharacters", "FunctionName")

package csc.markobot.dsl

import csc.markobot.api.Schedule
import csc.markobot.api.WeekDay

fun MakroBotScenario.schedule(schedule: Schedule.() -> Unit): MakroBotScenario {
    this.schedule = Schedule().apply(schedule)
    return this
}

fun MakroBotScenario.restart_schedule(): MakroBotScenario {
    this.schedule = null
    return this
}

typealias time = Pair<WeekDay, Int>

infix fun WeekDay.at(hour: Int) = time(this, hour)

fun Schedule.repeat(vararg timePointsToAdd: time) {
    timePoints.addAll(timePointsToAdd)
}

infix fun ClosedRange<WeekDay>.at(hour: Int): List<time> {
    return WeekDay.values().filter { it in this }.map { time(it, hour) }           // can't iterate over ClosedRange
}

fun Schedule.repeat(timePointsToAdd: List<time>) = repeat(*timePointsToAdd.toTypedArray())

fun Schedule.except(vararg daysOfMonth: Int) {
    exceptDaysOfMonth.addAll(daysOfMonth.toList())
}
