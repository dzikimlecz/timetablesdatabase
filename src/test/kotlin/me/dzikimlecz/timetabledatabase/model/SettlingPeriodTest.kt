package me.dzikimlecz.timetabledatabase.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

internal class SettlingPeriodTest {

    @Test
    fun `should fail to create SettlingPeriod of strat equal or after an end`() {
        // when
        val date0 = LocalDate.of(2021, 1, 1)
        val date1 = LocalDate.of(2020, 1, 1)
        // then
        assertThrows<IllegalArgumentException> { SettlingPeriod(date0, date0) }
        assertThrows<IllegalArgumentException> { SettlingPeriod(date0, date1) }
    }

    @Test
    fun `should produce valid string form and reconstruct an objet from it`() {
        // given
        val date0 = LocalDate.of(2020, 1, 1)
        val date1 = LocalDate.of(2021, 1, 1)
        // when
        val period = SettlingPeriod(date0, date1)
        val string = period.toString()
        // then
        assertTrue(SettlingPeriod.validate(string))
        var x: Any? = null
        assertDoesNotThrow { x = SettlingPeriod.of(string) }
        assertEquals(x, period)
    }

    @Test
    fun `should fail to create SettlingPeriod from invalid string`() {
        // when
        val string = "invalid"
        // then
        assertThrows<IllegalArgumentException> { SettlingPeriod.of(string) }
    }
    
}