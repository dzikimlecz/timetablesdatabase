package me.dzikimlecz.timetabledatabase.model

import com.fasterxml.jackson.annotation.JsonIgnore

class Lecturer(
    val name: String,
    val code: String =
        name.split(Regex("\\s"))
            .joinToString(separator = "") { it[0].toString().toUpperCase() },
    val hoursWorkedStrings: Map<String, Int>,
) {

    @JsonIgnore
    val hoursWorked: Map<SettlingPeriod, Int> = hoursWorkedStrings.mapKeys { SettlingPeriod.of(it.key) }

    @JsonIgnore
    val totalHoursWorked = hoursWorked.values.stream().mapToInt { it.toInt() }.sum()

    inline fun derive(
        name: String = this.name,
        code: String = this.code,
        instruction: MutableMap<SettlingPeriod, Int>.() -> Unit,
    ) = Lecturer(name, code, HashMap(hoursWorked).apply(instruction).mapKeys { it.key.toString() })

    companion object {
        fun of(name: String,
               code: String =
                   name.split(Regex("\\s"))
                       .joinToString(separator = "") { it[0].toString().toUpperCase() },
               hoursWorked: Map<SettlingPeriod, Int>
        ) = Lecturer(name, code, HashMap(hoursWorked).mapKeys { it.key.toString() })
    }


}
