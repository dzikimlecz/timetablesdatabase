package me.dzikimlecz.timetabledatabase.model.database

import me.dzikimlecz.timetabledatabase.model.TimeTable
import java.util.*
import kotlin.NoSuchElementException

interface TimeTablesDataSource: DataSourceOf<TimeTable> {

    override fun create(table: TimeTable): TimeTable {
        require(findById(table.name).isEmpty) { "Table of name: ${table.name} already exists." }
        return save(table)
    }

    override fun patch(table: TimeTable): TimeTable {
        findById(table.name).orElseThrow { NoSuchElementException("Table of name: ${table.name} does not exist.") }
        return save(table)
    }

    fun findByName(name: String): Optional<TimeTable>

    override fun retrieve(key: String): TimeTable =
        findByName(key).orElseThrow { NoSuchElementException("Table of name: $key does not exist.") }

}
