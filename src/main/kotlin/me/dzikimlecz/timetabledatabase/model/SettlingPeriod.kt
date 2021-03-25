package me.dzikimlecz.timetabledatabase.model

import java.time.LocalDate

data class SettlingPeriod(
    val start: LocalDate,
    val end: LocalDate,
) {
    init {
        if (start >= end)
            throw IllegalArgumentException("Start date: $start must be earlier than end date: $end")
    }
}
