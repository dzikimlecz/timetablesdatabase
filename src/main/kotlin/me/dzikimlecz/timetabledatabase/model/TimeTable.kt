package me.dzikimlecz.timetabledatabase.model

import me.dzikimlecz.timetables.TimeSpan
import java.time.LocalDate

data class TimeTable(
    val date: LocalDate,
    val name: String,
    val table: List<List<Cell>>,
    val timeSpans : List<List<TimeSpan?>>,
)



