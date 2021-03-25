package me.dzikimlecz.timetabledatabase.model.database

import me.dzikimlecz.timetabledatabase.model.Lecturer

interface LecturersDataSource {

    fun retrieveLecturers() : Collection<Lecturer>

    fun retrieveLecturers(predicate: (Lecturer) -> Boolean) = retrieveLecturers().filter(predicate)
}