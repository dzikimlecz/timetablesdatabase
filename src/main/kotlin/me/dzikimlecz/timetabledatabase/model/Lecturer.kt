package me.dzikimlecz.timetabledatabase.model

import me.dzikimlecz.lecturers.LecturerTransferredSurrogate
import me.dzikimlecz.lecturers.SettlingPeriod
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

data class Lecturer(
    val name: String,
    val code: String =
        name.split(Regex("\\s"))
            .joinToString(separator = "") { it[0].toString().uppercase() },
    val hoursWorked: Map<SettlingPeriod, Int>,
    @Id val id: ObjectId? = null,
) {

    inline fun derive(
        name: String = this.name,
        code: String = this.code,
        instruction: MutableMap<SettlingPeriod, Int>.() -> Unit = {},
    ) = Lecturer(name, code, HashMap(hoursWorked).apply(instruction), id)

    fun toSurrogate(): LecturerTransferredSurrogate =
        LecturerTransferredSurrogate(name, code, hoursWorked.mapKeys { it.key.toString() })
}

fun me.dzikimlecz.lecturers.Lecturer.toLocalImplementation() = Lecturer(
    name, code, hoursWorked
)