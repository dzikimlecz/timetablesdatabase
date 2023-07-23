package me.dzikimlecz.timetabledatabase.model

import com.fasterxml.jackson.annotation.JsonIgnore
import me.dzikimlecz.lecturers.LecturerTransferredSurrogate
import me.dzikimlecz.lecturers.SettlingPeriod
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

data class Lecturer(
    val name: String,
    val code: String =
        name.split(Regex("\\s"))
            .joinToString(separator = "") { it[0].toString().uppercase() },
    val hoursWorked: Map<String, Int>,
    @JsonIgnore @Id var id: ObjectId? = null,
) {

    inline fun derive(
        name: String = this.name,
        code: String = this.code,
        instruction: MutableMap<SettlingPeriod, Int>.() -> Unit = {},
    ) = Lecturer(
        name, code,
        hoursWorked.mapKeys { SettlingPeriod.of(it.key) }.toMutableMap().apply(instruction).mapKeys { it.key.toString() },
        id
    )

    fun toSurrogate(): LecturerTransferredSurrogate =
        LecturerTransferredSurrogate(name, code, hoursWorked.mapKeys { it.key })

    fun toGeneralImplementation(): me.dzikimlecz.lecturers.Lecturer =
        me.dzikimlecz.lecturers.Lecturer(name, code, hoursWorked.mapKeys { SettlingPeriod.of(it.key) })

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Lecturer

        if (name != other.name) return false
        if (code != other.code) return false
        if (id != other.id) return false
        if (hoursWorked != other.hoursWorked) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + code.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + hoursWorked.hashCode()
        return result
    }


}

fun me.dzikimlecz.lecturers.Lecturer.toLocalImplementation() = Lecturer(
    name, code, hoursWorked.mapKeys { it.key.toString() }
)

fun dropOmittedInfoChars(code: String) =
    """\[.*]|\**""".toRegex().replace(code, "")