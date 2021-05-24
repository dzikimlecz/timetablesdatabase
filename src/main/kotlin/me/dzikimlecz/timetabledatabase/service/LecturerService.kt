package me.dzikimlecz.timetabledatabase.service

import me.dzikimlecz.lecturers.LecturerTransferredSurrogate
import me.dzikimlecz.lecturers.SettlingPeriod
import me.dzikimlecz.timetabledatabase.model.Cell
import me.dzikimlecz.timetabledatabase.model.Lecturer
import me.dzikimlecz.timetabledatabase.model.TimeTable
import me.dzikimlecz.timetabledatabase.model.database.LecturersDataSource
import me.dzikimlecz.timetabledatabase.model.toLocalImplementation
import org.springframework.stereotype.Service

@Service
class LecturerService(private val dataSource: LecturersDataSource) {
    fun getLecturers(): Collection<LecturerTransferredSurrogate> =
        dataSource.findAll().map { it.toSurrogate() }

    fun getLecturer(key: String): LecturerTransferredSurrogate {
        val optionalResult =
            if (key.none { it.isWhitespace() }) dataSource.findByCode(key)
            else dataSource.findByName(key)
        return optionalResult.orElseThrow { NoSuchElementException("Could not find lecturer $key") }.toSurrogate()
    }

    fun addLecturer(lecturer: LecturerTransferredSurrogate): LecturerTransferredSurrogate {
        expectNotPresent(lecturer.code)
        return save(lecturer)
    }

    fun patchLecturer(lecturer: LecturerTransferredSurrogate): LecturerTransferredSurrogate {
        expectPresent(lecturer.code)
        return save(lecturer)
    }

    fun deleteLecturer(key: String): LecturerTransferredSurrogate {
        val lecturer = expectPresent(key)
        dataSource.delete(lecturer)
        return lecturer.toSurrogate()
    }

    fun addTimeWorked(table: TimeTable) =
        updateTimeWorked(table) { a, b -> a + b }

    fun subtractTimeWorked(table: TimeTable) =
        updateTimeWorked(table) { a, b -> a - b }

    private fun updateTimeWorked(table: TimeTable, operator: (Int, Int) -> Int) {
        val minutesWorked = collectTimeWorked(table)
        val period: SettlingPeriod = table.period()
        for ((code, minutes) in minutesWorked)
            dataSource.findByCode(code).get().derive {
                merge(period, minutes, operator)
            }.also { dataSource.save(it) }
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
                foundCodes += dataSource.findByCode(code).get().code
            } catch (e: NoSuchElementException) { notFoundCodes += code }
        if (notFoundCodes.isNotEmpty()) throw LecturerNotFoundException(notFoundCodes)
    }

    private fun save(lecturer: LecturerTransferredSurrogate): LecturerTransferredSurrogate =
        dataSource.save(lecturer.toLecturer().toLocalImplementation()).toSurrogate()

    private fun expectPresent(code: String): Lecturer {
        val expectedPresent = dataSource.findByCode(code)
        return expectedPresent.orElseThrow { NoSuchElementException("Lecturer: $code does not exist") }
    }

    private fun expectNotPresent(code: String) {
        val expectedEmpty = dataSource.findByCode(code)
        require(expectedEmpty.isEmpty) { "Lecturer: $code already exists" }
    }

}

private fun TimeTable.period(): SettlingPeriod {
    val start = this.date
    val days = this.table.size - 1
    val end = start.plusDays(days.toLong())
    return SettlingPeriod(start, end)
}
