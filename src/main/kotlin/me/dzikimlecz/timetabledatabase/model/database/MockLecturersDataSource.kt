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
        return try {
            if (key.none { it.isWhitespace() }) searchByCode(key)
            else searchByName(key)
        } catch(e: NoSuchElementException) { throw NoSuchElementException("Could not find lecturer $key") }
    }

    fun searchByName(name: String): Lecturer =
        retrieve().first { it.name.equals(name, ignoreCase = true) }

    fun searchByCode(name: String): Lecturer =
        retrieve().first { it.code.equals(name, ignoreCase = true) }

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