package me.dzikimlecz.timetabledatabase.model.database

import me.dzikimlecz.lecturers.Lecturer
import org.springframework.stereotype.Repository

@Repository
class MockLecturersDataSource : LecturersDataSource {

    private val lecturers = mutableListOf(
        Lecturer("Marcin Najman", hoursWorked = emptyMap()),
        Lecturer("Jan Paweł II", hoursWorked = emptyMap()),
        Lecturer("Józef Stalin", hoursWorked = emptyMap()),
        Lecturer("Jeff Bezos", hoursWorked = emptyMap()),
        Lecturer("Twój Stary", hoursWorked = emptyMap()),
    )

    override fun retrieve(): Collection<Lecturer> = lecturers

    override fun retrieve(key: String): Lecturer {
        if (key.count { it.isWhitespace() } > 2) try {
            return retrieve().first { it.name.equals(key, ignoreCase = true) }
        } catch(_: NoSuchElementException) {}
        return retrieve().firstOrNull { it.code.equals(key, ignoreCase = true) }
            ?: throw NoSuchElementException("Could not find lecturer $key")
    }

    override fun create(lecturer: Lecturer): Lecturer {
        require (lecturers.none { it.code == lecturer.code }) { "Lecturer of code ${lecturer.code} already exists." }
        lecturers += lecturer
        return lecturer
    }

    override fun patch(lecturer: Lecturer): Lecturer {
        val indexToPatch = lecturers.indexOfFirst { it.code == lecturer.code }
        if (indexToPatch == -1) throw NoSuchElementException("There is no lecturer of code: ${lecturer.code}")
        lecturers[indexToPatch] = lecturers[indexToPatch].derive(lecturer.name) {
            clear()
            this += lecturer.hoursWorked
        }
        return lecturers[indexToPatch]
    }

    override fun delete(key: String): Lecturer {
        val removed = lecturers.firstOrNull { it.code == key }
            ?: throw NoSuchElementException("There is no lecturer of code: $key")
        lecturers -= removed
        return removed
    }


}