package me.dzikimlecz.timetabledatabase.model.database

import me.dzikimlecz.timetabledatabase.model.Lecturer
import org.springframework.stereotype.Repository

@Repository
class MockLecturersDataSource : LecturersDataSource {

    private val lecturers = mutableListOf(
        Lecturer("Marcin Najman", hoursWorkedStrings = emptyMap()),
        Lecturer("Jan Paweł II", hoursWorkedStrings = emptyMap()),
        Lecturer("Józef Stalin", hoursWorkedStrings = emptyMap()),
        Lecturer("Jeff Bezos", hoursWorkedStrings = emptyMap()),
        Lecturer("Twój Stary", hoursWorkedStrings = emptyMap()),
    )

    override fun retrieve(): Collection<Lecturer> = lecturers
    override fun create(e: Lecturer): Lecturer {
        require (lecturers.none { it.code == e.code }) { "Lecturer of code ${e.code} already exists." }
        lecturers.add(e)
        return e
    }

    override fun patch(e: Lecturer): Lecturer {
        val indexToPatch = lecturers.indexOfFirst { it.code == e.code }
        if (indexToPatch == -1) throw NoSuchElementException("There is no lecturer of code: ${e.code}")
        lecturers[indexToPatch] = lecturers[indexToPatch].derive(e.name) {
            clear()
            this += e.hoursWorked
        }
        return lecturers[indexToPatch]
    }

    override fun delete(key: String): Lecturer {
        val removed = try {
            lecturers.first { it.code == key }
        } catch(e: NoSuchElementException) {
            throw NoSuchElementException("There is no lecturer of code: $key")
        }
        lecturers.remove(removed)
        return removed
    }


}