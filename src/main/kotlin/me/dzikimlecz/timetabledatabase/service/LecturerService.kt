package me.dzikimlecz.timetabledatabase.service

import me.dzikimlecz.timetabledatabase.model.Lecturer
import me.dzikimlecz.timetabledatabase.model.database.LecturersDataSource
import org.springframework.stereotype.Service

@Service
class LecturerService(private val dataSource: LecturersDataSource) {
    fun getLecturers() = dataSource.retrieve()
    fun getLecturer(key: String): Lecturer = dataSource.retrieve(key)
    fun addLecturer(lecturer: Lecturer): Lecturer = dataSource.create(lecturer)

    fun patchLecturer(lecturer: Lecturer) = dataSource.patch(lecturer)
    fun deleteLecturer(key: String) = dataSource.delete(key)
}