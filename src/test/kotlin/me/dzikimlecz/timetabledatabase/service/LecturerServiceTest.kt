package me.dzikimlecz.timetabledatabase.service

import io.mockk.mockk
import io.mockk.verify
import me.dzikimlecz.timetabledatabase.model.database.LecturersDataSource
import org.junit.jupiter.api.Test

internal class LecturerServiceTest {

    private val dataSource: LecturersDataSource = mockk(relaxed = true)
    private val lecturerService = LecturerService(dataSource)

    @Test
    fun `should call data source to retrieve lecturers`() {
        //when
            lecturerService.lecturers
        //then
            verify { dataSource.findAll() }
    }
}