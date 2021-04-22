package me.dzikimlecz.timetabledatabase.model.database

import me.dzikimlecz.timetabledatabase.model.Lecturer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

internal class MockLecturersDataSourceTest {

    private val lecturersSource: MockLecturersDataSource = MockLecturersDataSource()

    @Test
    fun `should provide collection of lecturers`() {
        //when
            val lecturers = lecturersSource.retrieve()
        //then
            assertThat(lecturers).isNotEmpty
    }

    @Test
    fun `should provide some mock data`() {
        //when
            val lecturers = lecturersSource.retrieve()
        //then
            assertThat(lecturers).allMatch { it.code.isNotBlank() && it.name.isNotBlank() }
    }

    @Test
    fun `should create new Lecturer from given data`() {
        // when
        val lecturer = Lecturer("New Guy", hoursWorked = emptyMap())
        // then
        assertDoesNotThrow { lecturersSource.create(lecturer) }
        var retrieved: Lecturer? = null
        assertDoesNotThrow { retrieved = lecturersSource.retrieve(lecturer.code) }
        assertEquals(lecturer, retrieved)
    }

    @Test
    fun `should override Lecturer with given data`() {
        // when
        val lecturer = lecturersSource.retrieve().first().derive(name = "Julius Caesar")
        // then
        assertDoesNotThrow { lecturersSource.patch(lecturer) }
        var retrieved: Lecturer? = null
        assertDoesNotThrow { retrieved = lecturersSource.retrieve(lecturer.code) }
        assertEquals(lecturer, retrieved)
    }

    @Test
    fun `should delete lecturer of given code`() {
        // when
        val code = lecturersSource.retrieve().first().code
        // then
        assertDoesNotThrow { lecturersSource.delete(code) }
        assertThrows<NoSuchElementException> { lecturersSource.retrieve(code) }

    }
}
