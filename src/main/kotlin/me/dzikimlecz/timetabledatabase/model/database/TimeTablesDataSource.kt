package me.dzikimlecz.timetabledatabase.model.database

import me.dzikimlecz.timetabledatabase.model.TimeTable

interface TimeTablesDataSource: DataSource<TimeTable> {

    override fun create(table: TimeTable): TimeTable

    override fun patch(table: TimeTable): TimeTable
}
