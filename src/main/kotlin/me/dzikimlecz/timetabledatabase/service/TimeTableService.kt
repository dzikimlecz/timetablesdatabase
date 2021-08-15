package me.dzikimlecz.timetabledatabase.service

import me.dzikimlecz.timetabledatabase.model.TimeTable
import me.dzikimlecz.timetabledatabase.model.database.TimeTablesDataSource
import org.springframework.stereotype.Service

@Service
class TimeTableService(
    private val dataSource: TimeTablesDataSource,
    private val lecturerService: LecturerService,
) {

    val timeTables: Collection<TimeTable>
        get() = dataSource.findAll()

    fun getTimeTable(key: String): TimeTable {
        expectPresent(key)
        return dataSource.findByName(key.trim()).get()
    }

    fun addTimeTable(table: TimeTable): TimeTable {
        expectNotPresent(table.name)
        lecturerService.verifyAllLecturersFound(table.table)
        lecturerService.addTimeWorked(table)
        return dataSource.save(table)
    }

    fun patchTimeTable(table: TimeTable): TimeTable {
        val oldTable = expectPresent(table.name)
        lecturerService.verifyAllLecturersFound(table.table)
        lecturerService.subtractTimeWorked(oldTable)
        lecturerService.addTimeWorked(table)
        return dataSource.save(table)
    }

    fun deleteTimeTable(key: String): TimeTable {
        val oldTable = expectPresent(key)
        lecturerService.subtractTimeWorked(oldTable)
        dataSource.delete(oldTable)
        return oldTable
    }


    private fun expectPresent(name: String): TimeTable =
        dataSource.findByName(name).orElseThrow { NoSuchElementException("Table: $name does not exist") }

    private fun expectNotPresent(name: String) {
        val expectedEmpty = dataSource.findByName(name)
        require(expectedEmpty.isEmpty) { "Table: $name already exists" }
    }

}

class LecturerNotFoundException(val keys: List<String>): RuntimeException() {
    init { require (keys.isNotEmpty()) { "Keys can't be empty" } }
}