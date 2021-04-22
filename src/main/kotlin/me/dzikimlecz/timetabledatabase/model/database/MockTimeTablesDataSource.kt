package me.dzikimlecz.timetabledatabase.model.database

import me.dzikimlecz.timetabledatabase.model.TimeTable
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class MockTimeTablesDataSource: TimeTablesDataSource {

    private val tables = mutableListOf(
        TimeTable(
            LocalDate.now(),
            "test",
            listOf(),
            listOf(),
        )
    )

    override fun retrieve(): Collection<TimeTable> = tables.toList()

    override fun retrieve(key: String): TimeTable =
        tables.firstOrNull { it.name == key }
            ?: throw NoSuchElementException("There is no timetable of name $key")

    override fun create(table: TimeTable): TimeTable {
        require(tables.none { it.name == table.name }) { "Table of name ${table.name} already exists" }
        tables += table
        return table
    }

    override fun patch(table: TimeTable): TimeTable {
        val i = tables.indexOfFirst { it.name == table.name }
            .takeUnless { it < 0 }
            ?: throw NoSuchElementException("There is no timetable of name ${table.name} and date ${table.date}")
        tables[i] = table
        return tables[i]
    }

    override fun delete(key: String): TimeTable {
        val timeTable = tables.firstOrNull { it.name == key }
            ?: throw NoSuchElementException("There is no timetable of name $key")
        tables -= timeTable
        return timeTable
    }

}