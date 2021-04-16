package me.dzikimlecz.timetabledatabase.model.database

import me.dzikimlecz.timetabledatabase.model.Lecturer

interface LecturersDataSource : DataSource<Lecturer> {

    fun retrieve(predicate: (Lecturer) -> Boolean): Collection<Lecturer>

}