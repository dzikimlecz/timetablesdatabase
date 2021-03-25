package me.dzikimlecz.timetabledatabase.model

data class Lecturer(
    val name: String,
    val code: String = name.substring(0..1),
    val hoursWorked: Map<SettlingPeriod, Int>,
) {
    val totalHoursWorked = hoursWorked.values.stream().mapToInt { it.toInt() }.sum()

    inline fun derive(
        name: String = this.name,
        code: String = this.code,
        instruction: MutableMap<SettlingPeriod, Int>.() -> Unit,
    ): Lecturer {
        val mutableCopy = HashMap<SettlingPeriod, Int>(hoursWorked)
        mutableCopy.instruction()
        return Lecturer(name, code, mutableCopy)
    }


}
