package me.dzikimlecz.timetabledatabase.model.database

import me.dzikimlecz.lecturers.Lecturer

interface LecturersDataSource : DataSource<Lecturer> {

    /**
     * Should return lecturer of given [key]
     *
     * if it doesn't contains any whitespace treats it as the code of a lecturer, otherwise as a name
     */
    override fun retrieve(key: String): Lecturer

    override fun create(lecturer: Lecturer): Lecturer

    override fun patch(lecturer: Lecturer): Lecturer
}