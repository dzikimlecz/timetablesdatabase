package me.dzikimlecz.timetabledatabase.service

import me.dzikimlecz.timetabledatabase.model.Cell
import me.dzikimlecz.timetabledatabase.model.TimeTable
import me.dzikimlecz.timetabledatabase.model.database.LecturersDataSource
import me.dzikimlecz.timetabledatabase.model.database.TimeTablesDataSource
import org.springframework.stereotype.Service

@Service
class TimeTableService(
    private val tablesSource: TimeTablesDataSource,
    private val lecturerService: LecturerService,
) {

    fun getTimeTables(): Collection<TimeTable> =
        tablesSource.retrieve()

    fun getTimeTable(key: String): TimeTable =
        tablesSource.retrieve(key.trim())

    fun addTimeTable(table: TimeTable): TimeTable {
        lecturerService.verifyAllLecturersFound(table.table)
        lecturerService.addTimeWorked(table)
        return tablesSource.create(table)
    }

    fun patchTimeTable(table: TimeTable): TimeTable {
        lecturerService.verifyAllLecturersFound(table.table)
        val oldTable = tablesSource.retrieve(table.name)
        lecturerService.subtractTimeWorked(oldTable)
        lecturerService.addTimeWorked(table)
        return tablesSource.patch(table)
    }

    fun deleteTimeTable(key: String): TimeTable {
        val oldTable = tablesSource.retrieve(key)
        lecturerService.subtractTimeWorked(oldTable)
        return tablesSource.delete(key)
    }

}

class LecturerNotFoundException(val keys: List<String>): RuntimeException() {
    init { require (keys.isNotEmpty()) { "Keys can't be empty" } }
}