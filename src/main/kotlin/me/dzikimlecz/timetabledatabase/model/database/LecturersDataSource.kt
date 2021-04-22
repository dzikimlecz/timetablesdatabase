package me.dzikimlecz.timetabledatabase.model.database

import me.dzikimlecz.timetabledatabase.model.Lecturer

interface LecturersDataSource : DataSource<Lecturer> {

    override fun create(lecturer: Lecturer): Lecturer

    override fun patch(lecturer: Lecturer): Lecturer
}