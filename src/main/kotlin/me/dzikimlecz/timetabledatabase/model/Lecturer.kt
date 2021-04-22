package me.dzikimlecz.timetabledatabase.model

import me.dzikimlecz.lecturers.LecturerTransferredSurrogate

data class Lecturer(
    val name: String,
    val code: String =
        name.split(Regex("\\s"))
            .joinToString(separator = "") { it[0].toString().toUpperCase() },
    val hoursWorked: Map<SettlingPeriod, Int>
) {


    inline fun derive(
        name: String = this.name,
        code: String = this.code,
        instruction: MutableMap<SettlingPeriod, Int>.() -> Unit = {},
    ) = Lecturer(name, code, HashMap(hoursWorked).apply(instruction))

    fun toSurrogate(): LecturerTransferredSurrogate =
        LecturerTransferredSurrogate(name, code, hoursWorked.mapKeys { it.key.toString() })
}

fun LecturerTransferredSurrogate.toLecturer() =
    Lecturer(name, code, hoursWorked.mapKeys { SettlingPeriod.of(it.key) })