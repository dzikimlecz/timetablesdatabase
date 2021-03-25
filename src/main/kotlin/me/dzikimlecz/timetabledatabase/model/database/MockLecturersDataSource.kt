package me.dzikimlecz.timetabledatabase.model.database

import me.dzikimlecz.timetabledatabase.model.Lecturer
import org.springframework.stereotype.Repository

@Repository
class MockLecturersDataSource : LecturersDataSource {

    private val lecturers = mutableListOf(Lecturer("A", "A", emptyMap()))

    override fun retrieveLecturers(): Collection<Lecturer> = lecturers

}