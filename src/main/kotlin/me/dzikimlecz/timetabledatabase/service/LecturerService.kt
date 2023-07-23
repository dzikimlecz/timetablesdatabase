package me.dzikimlecz.timetabledatabase.service

import me.dzikimlecz.lecturers.LecturerTransferredSurrogate
import me.dzikimlecz.lecturers.SettlingPeriod
import me.dzikimlecz.timetabledatabase.model.*
import me.dzikimlecz.timetabledatabase.model.DivisionDirection.HORIZONTAL
import me.dzikimlecz.timetabledatabase.model.database.LecturersDataSource
import org.springframework.stereotype.Service
import kotlin.math.max

@Service
class LecturerService(private val dataSource: LecturersDataSource) {
    val lecturers: Collection<LecturerTransferredSurrogate>
        get() = dataSource.findAll().map { it.toSurrogate() }

    fun getLecturer(key: String): LecturerTransferredSurrogate {
        val optionalResult =
            if (key.none { it.isWhitespace() })
                dataSource.findByCode(key.uppercase())
            else dataSource.findByName(key)
        return optionalResult.orElseThrow { NoSuchElementException("Could not find lecturer $key") }.toSurrogate()
    }

    fun addLecturer(lecturer: LecturerTransferredSurrogate): LecturerTransferredSurrogate {
        expectNotPresent(lecturer.code)
        return save(lecturer)
    }

    fun patchLecturer(lecturer: LecturerTransferredSurrogate): LecturerTransferredSurrogate {
        dataSource.delete(expectPresent(lecturer.code))
        return save(lecturer)
    }

    fun deleteLecturer(key: String): LecturerTransferredSurrogate {
        val lecturer = expectPresent(key)
        dataSource.delete(lecturer)
        return lecturer.toSurrogate()
    }

    fun addTimeWorked(table: TimeTable) =
        updateTimeWorked(table) { a, b -> max(a + b, 0) }

    fun subtractTimeWorked(table: TimeTable) =
        updateTimeWorked(table) { a, b -> max(a - b, 0) }

    private fun updateTimeWorked(table: TimeTable, operator: (Int, Int) -> Int) {
        val minutesWorked = collectTimeWorked(table)
        val period: SettlingPeriod = table.period() ?: return
        for ((code, minutes) in minutesWorked) {
            val oldOne = dataSource.findByCode(code.uppercase()).orElse(null) ?: continue
            oldOne.toGeneralImplementation()
                .derive { merge(period, minutes, operator) }
                .toLocalImplementation()
                .also { patchLecturer(it.toSurrogate()) }
        }
    }

    private fun collectTimeWorked(table: TimeTable): Map<String, Int> {
        val durations = table.timeSpans.map { list -> list.map { it?.minutes?.toInt() ?: 0 } }
        if (durations.size != table.table.size) {
            throw IllegalArgumentException("Malformed table")
        }
        val minutesWorked = mutableMapOf<String, Int>()
        for (row in table.table) {
            for ((i, pair) in row.withIndex()) {
                val columnDurations = durations[i]
                for ((j, rawCode) in pair.withIndex()) {
                    val code = dropOmittedInfoChars(rawCode)
                    if (code.isNotBlank()) {
                        val index = if (pair.divisionOrientation == HORIZONTAL) j else 0
                        minutesWorked.merge(code.trim(), columnDurations[index]) { a, b -> a + b }
                    }
                }
            }
        }
        return minutesWorked
    }

    fun verifyAllLecturersFound(table: List<List<Cell>>) {
        val notFoundCodes = mutableListOf<String>()
        val foundCodes = mutableListOf<String>()
        for (row in table) {
            for (cell in row) {
                for (rawCode in cell) {
                    val code = dropOmittedInfoChars(rawCode)
                    if (code.isNotBlank() && code !in foundCodes) {
                        try {
                            foundCodes += dataSource.findByCode(code.uppercase()).get().code
                        } catch (e: NoSuchElementException) { notFoundCodes += code }
                    }
                }
            }
        }
        if (notFoundCodes.isNotEmpty()) throw LecturerNotFoundException(notFoundCodes)
    }

    private fun save(lecturer: LecturerTransferredSurrogate): LecturerTransferredSurrogate {
        val lecturer1 = lecturer.toLecturer()
            .toLocalImplementation()
            .derive(code = lecturer.code.uppercase())
        return dataSource.save(lecturer1).toSurrogate()
    }

    private fun expectPresent(code: String): Lecturer {
        val expectedPresent = dataSource.findByCode(dropOmittedInfoChars(code.uppercase()))
        return expectedPresent.orElseThrow { NoSuchElementException("Lecturer: $code does not exist") }
    }

    private fun expectNotPresent(code: String) {
        val expectedEmpty = dataSource.findByCode(dropOmittedInfoChars(code.uppercase()))
        require(expectedEmpty.isEmpty) { "Lecturer: $code already exists" }
    }

}

private fun TimeTable.period(): SettlingPeriod? {
    val start = this.date
    val days = this.table.size - 1
    val end = start.plusDays(days.toLong())
    return try { SettlingPeriod(start, end) }
    catch (e: Exception) { null }
}
