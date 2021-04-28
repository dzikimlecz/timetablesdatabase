package me.dzikimlecz.timetabledatabase.service

import me.dzikimlecz.lecturers.LecturerTransferredSurrogate
import me.dzikimlecz.timetabledatabase.model.database.LecturersDataSource
import org.springframework.stereotype.Service

@Service
class LecturerService(private val dataSource: LecturersDataSource) {
    fun getLecturers(): Collection<LecturerTransferredSurrogate> = dataSource.retrieve().map { it.toSurrogate() }

    fun getLecturer(key: String) = dataSource.retrieve(key).toSurrogate()

    fun addLecturer(lecturer: LecturerTransferredSurrogate) = dataSource.create(lecturer.toLecturer()).toSurrogate()

    fun patchLecturer(lecturer: LecturerTransferredSurrogate) = dataSource.patch(lecturer.toLecturer()).toSurrogate()

    fun deleteLecturer(key: String) = dataSource.delete(key).toSurrogate()
}