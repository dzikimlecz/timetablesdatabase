package me.dzikimlecz.timetabledatabase.model.database

import me.dzikimlecz.timetabledatabase.model.TimeTable
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface TimeTablesDataSource: MongoRepository<TimeTable, ObjectId> {
    fun findByName(name: String): Optional<TimeTable>
}
