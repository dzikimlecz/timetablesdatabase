package me.dzikimlecz.timetabledatabase.service

import me.dzikimlecz.timetabledatabase.model.Cell
import me.dzikimlecz.timetabledatabase.model.TimeTable
import me.dzikimlecz.timetabledatabase.model.database.LecturersDataSource
import me.dzikimlecz.timetabledatabase.model.database.TimeTablesDataSource
import org.springframework.stereotype.Service

@Service
class TimeTableService(
    private val tablesSource: TimeTablesDataSource,
    private val lecturersSource: LecturersDataSource,
    private val lecturerService: LecturerService,
) {

    fun getTimeTables(): Collection<TimeTable> =
        tablesSource.retrieve()

    fun getTimeTable(key: String): TimeTable =
        tablesSource.retrieve(key.trim())

    fun addTimeTable(table: TimeTable): TimeTable {
        return tablesSource.create(table)
    }

    fun patchTimeTable(table: TimeTable): TimeTable {
        return tablesSource.patch(table)
    }

    fun deleteTimeTable(key: String): TimeTable =
        tablesSource.delete(key)

    fun verifyAllLecturersFound(table: List<List<Cell>>) {
        val notFoundCodes = mutableListOf<String>()
        val foundCodes = mutableListOf<String>()
        for (row in table) for (cell in row)
            for (code in cell) if (code.isNotBlank() && code !in foundCodes) try {
                foundCodes += lecturersSource.retrieve(code).code
            } catch (e: NoSuchElementException) { notFoundCodes += code }
        if (notFoundCodes.isNotEmpty())
            throw LecturerNotFoundException(notFoundCodes)
    }
}

class LecturerNotFoundException(val keys: List<String>): RuntimeException() {
    init { require (keys.isNotEmpty()) { "Keys can't be empty" } }
}