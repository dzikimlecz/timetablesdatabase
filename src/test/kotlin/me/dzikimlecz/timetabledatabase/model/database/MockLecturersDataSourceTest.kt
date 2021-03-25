package me.dzikimlecz.timetabledatabase.model.database

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MockLecturersDataSourceTest {

    private val lecturersSource = MockLecturersDataSource()

    @Test
    fun `should provide collection of lecturers`() {
        //when
            val lecturers = lecturersSource.retrieveLecturers()
        //then
            assertThat(lecturers).isNotEmpty
    }

    @Test
    fun `should provide some mock data`() {
        //when
            val lecturers = lecturersSource.retrieveLecturers()
        //then
            assertThat(lecturers).allMatch { it.code.isNotBlank() && it.name.isNotBlank() }
    }
}