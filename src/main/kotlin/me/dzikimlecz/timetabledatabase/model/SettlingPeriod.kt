package me.dzikimlecz.timetabledatabase.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

data class SettlingPeriod(
    val start: LocalDate,
    val end: LocalDate,
) {
    init { require (start < end) { "Start date: $start must be earlier than the end date: $end" } }

    override fun toString(): String = "{${start.format(ISO_LOCAL_DATE)}::${end.format(ISO_LOCAL_DATE)}}"

    companion object {
        private val pattern = Regex("\\{\\d{4}(-\\d{2}){2}::\\d{4}(-\\d{2}){2}}")

        fun of(string: String): SettlingPeriod {
            require(validate(string)) { "Invalid value: $string" }
            val dates = string.trim().removePrefix("{").removeSuffix("}").split("::")
            return SettlingPeriod(LocalDate.parse(dates[0]), LocalDate.parse(dates[1]),)
        }

        fun of(start: LocalDate, end: LocalDate) = SettlingPeriod(start, end)

        fun validate(string: String) = pattern.matches(string.trim())
    }
}
