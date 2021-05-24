package me.dzikimlecz.timetabledatabase.service

import me.dzikimlecz.lecturers.LecturerTransferredSurrogate
import me.dzikimlecz.lecturers.SettlingPeriod
import me.dzikimlecz.timetabledatabase.model.Cell
import me.dzikimlecz.timetabledatabase.model.TimeTable
import me.dzikimlecz.timetabledatabase.model.database.LecturersDataSource
import me.dzikimlecz.timetabledatabase.model.toLocalImplementation
import org.springframework.stereotype.Service

@Service
class LecturerService(private val dataSource: LecturersDataSource) {
    fun getLecturers(): Collection<LecturerTransferredSurrogate> =
        dataSource.retrieve().map { it.toSurrogate() }

    fun getLecturer(key: String) =
        dataSource.retrieve(key).toSurrogate()

    fun addLecturer(lecturer: LecturerTransferredSurrogate) =
        dataSource.create(lecturer.toLecturer().toLocalImplementation()).toSurrogate()

    fun patchLecturer(lecturer: LecturerTransferredSurrogate) =
        dataSource.patch(lecturer.toLecturer().toLocalImplementation()).toSurrogate()

    fun deleteLecturer(key: String) = dataSource.delete(key).toSurrogate()

    fun addTimeWorked(table: TimeTable) =
        updateTimeWorked(table) { a, b -> a + b }

    fun subtractTimeWorked(table: TimeTable) =
        updateTimeWorked(table) { a, b -> a - b }

    private fun updateTimeWorked(table: TimeTable, operator: (Int, Int) -> Int) {
        val minutesWorked = collectTimeWorked(table)
        val period: SettlingPeriod = table.period()
        for ((code, minutes) in minutesWorked)
            dataSource.retrieve(code).derive {
                merge(period, minutes, operator)
            }.also { dataSource.patch(it) }
    }

    private fun collectTimeWorked(table: TimeTable): Map<String, Int> {
        val durations = table.timeSpans.map { list -> list.map { it?.minutes?.toInt() ?: 0 } }
        assert(durations.size == table.table.size)

        val minutesWorked = mutableMapOf<String, Int>()
        for (row in table.table) {
            for ((i, pair) in row.withIndex()) {
                val columnDurations = durations[i]
                for ((j, code) in pair.withIndex()) if (code.isNotBlank())
                    minutesWorked.merge(code, columnDurations[j]) { a, b -> a + b }
            }
        }

        return minutesWorked
    }

    fun verifyAllLecturersFound(table: List<List<Cell>>) {
        val notFoundCodes = mutableListOf<String>()
        val foundCodes = mutableListOf<String>()
        for (row in table) for (cell in row)
            for (code in cell) if (code.isNotBlank() && code !in foundCodes) try {
                foundCodes += dataSource.retrieve(code).code
            } catch (e: NoSuchElementException) { notFoundCodes += code }
        if (notFoundCodes.isNotEmpty()) throw LecturerNotFoundException(notFoundCodes)
    }
}

private fun TimeTable.period(): SettlingPeriod {
    val start = this.date
    val days = this.table.size - 1
    val end = start.plusDays(days.toLong())
    return SettlingPeriod(start, end)
}
