package me.dzikimlecz.timetabledatabase.service

import me.dzikimlecz.timetabledatabase.model.database.LecturersDataSource
import org.springframework.stereotype.Service

@Service
class LecturerService(private val dataSource: LecturersDataSource) {
    fun getLecturers() = dataSource.retrieveLecturers()
}