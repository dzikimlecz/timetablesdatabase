package me.dzikimlecz.timetabledatabase.service

import me.dzikimlecz.timetabledatabase.model.TimeTable
import me.dzikimlecz.timetabledatabase.model.database.TimeTablesDataSource
import org.springframework.stereotype.Service

@Service
class TimeTableService(private val dataSource: TimeTablesDataSource) {

    fun getTimeTables(): Collection<TimeTable> =
        dataSource.retrieve()

    fun getTimeTable(key: String): TimeTable =
        dataSource.retrieve(key)

    fun addTimeTable(table: TimeTable): TimeTable =
        dataSource.create(table)

    fun patchTimeTable(table: TimeTable): TimeTable =
        dataSource.patch(table)

    fun deleteTimeTable(key: String): TimeTable =
        dataSource.delete(key)

}
