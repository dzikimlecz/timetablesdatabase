package me.dzikimlecz.timetabledatabase.model.database

import me.dzikimlecz.timetabledatabase.model.Lecturer
import me.dzikimlecz.timetabledatabase.model.TimeTable
import java.util.*


interface LecturersDataSource : DataSourceOf<Lecturer> {

    /**
     * Finds a lecturer of the name same as the given one
     */
    fun findByName(name: String): Optional<Lecturer>

    /**
     * Finds a lecturer of the code same as the given one
     */
    fun findByCode(code: String): Optional<Lecturer>

    override fun retrieve(key: String): Lecturer {
        val optionalResult =
            if (key.none { it.isWhitespace() }) findByCode(key)
            else findByName(key)
        return optionalResult.orElseThrow { NoSuchElementException("Could not find lecturer $key") }
    }

    override fun create(lecturer: Lecturer): Lecturer

    override fun patch(lecturer: Lecturer): Lecturer
}