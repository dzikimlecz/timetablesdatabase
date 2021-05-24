package me.dzikimlecz.timetabledatabase.model.database

import me.dzikimlecz.timetabledatabase.model.Lecturer
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*


interface LecturersDataSource : MongoRepository<Lecturer, ObjectId> {

    /**
     * Finds a lecturer of the name same as the given one
     */
    fun findByName(name: String): Optional<Lecturer>

    /**
     * Finds a lecturer of the code same as the given one
     */
    fun findByCode(code: String): Optional<Lecturer>

}