package me.dzikimlecz.timetabledatabase.model




data class Cell(
    val content: List<String>,
    val divisionOrientation: DivisionDirection?,
)

enum class DivisionDirection {
    VERTICAL, HORIZONTAL, NONE;

    companion object {
        fun of(value: String?) = valueOf(value?.toUpperCase() ?: "NONE")
    }
}