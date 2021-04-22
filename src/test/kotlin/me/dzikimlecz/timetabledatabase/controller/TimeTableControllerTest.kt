package me.dzikimlecz.timetabledatabase.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dzikimlecz.timetabledatabase.model.TimeTable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.*
import java.time.LocalDate
import org.springframework.http.MediaType.APPLICATION_JSON as JSON

private const val baseUrl = "/timetableapi/timetables"

@SpringBootTest
@AutoConfigureMockMvc
internal class TimeTableControllerTest @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper
) {

    @Nested
    @DisplayName("Get TimeTables | $baseUrl")
    @TestInstance(PER_CLASS)
    inner class GetTimeTables {
        @Test
        fun `should retain all timetables`() {
            // when/then
                mockMvc.get(baseUrl)
                    .andDo { print() }
                    .andExpect {
                        status { isOk() }
                        content {
                            contentType(JSON)
                            jsonPath("$[0].name") { value("test") }
                        }
                    }
        }
    }

    @Nested
    @DisplayName("Get TimeTable | $baseUrl/{key}")
    @TestInstance(PER_CLASS)
    inner class GestTimeTable {
        @Test
        fun `should get one timetable of given name`() {
            // given
            val table = TimeTable(LocalDate.now(), "test", listOf(), listOf())
            // when/ then
            mockMvc.get("$baseUrl/${table.name}")
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { json(objectMapper.writeValueAsString(table)) }
                }
        }

        @Test
        fun `should fail to get nonexistent table`() {
            // given
            val key = "no tables like that 331_967.14146_028.71" // random stuff to make sure it won't be found
            // when/ then
            mockMvc.get("$baseUrl/$key")
                .andDo { print() }.andExpect { status { isNotFound() } }
        }
    }
    
    @Nested
    @DisplayName("Post TimeTable | $baseUrl | Content in the Body")
    @TestInstance(PER_CLASS)
    inner class PostTimeTable {
        @Test
        fun `should post new timetable`() {
            // given
            val timeTable = TimeTable(LocalDate.now(), "new table", listOf(), listOf())
            // when
            val post = mockMvc.post(baseUrl) {
                contentType = JSON
                content = objectMapper.writeValueAsString(timeTable)
            }
            // then
            post.andDo { print() }
                .andExpect {
                    status { isCreated() }
                    content { 
                        contentType(JSON)
                        json(objectMapper.writeValueAsString(timeTable)) 
                    }
                }
        }

        @Test
        fun `should fail to post a timetable of existing name`() {
            // given
            val timeTable = TimeTable(LocalDate.now(), "test", listOf(), listOf())
            // when
            val post = mockMvc.post(baseUrl) {
                contentType = JSON
                content = objectMapper.writeValueAsString(timeTable)
            }
            // then
            post.andDo { print() }
                .andExpect { status { isBadRequest() } }
        }
    }
    
    @Nested
    @DisplayName("Patch TimeTable | $baseUrl | Content in the body")
    @TestInstance(PER_CLASS)
    inner class PatchTimeTable {
        @Test
        fun `should patch an existing timetable`() {
            // given
            val timeTable = TimeTable(LocalDate.now().plusYears(2), "test", listOf(), listOf())
            // when
            val patch = mockMvc.patch(baseUrl) {
                contentType = JSON
                content = objectMapper.writeValueAsString(timeTable)
            }
            // then
            patch.andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { json(objectMapper.writeValueAsString(timeTable)) }
                }
        }

        @Test
        fun `should fail to patch nonexistent table`() {
            // given
            val timeTable =
                TimeTable(LocalDate.now(), "don't look for b8fe2e5b-d0b9-4514-9698-bb060e00cb97", listOf(), listOf())
            // when
            val patch = mockMvc.patch(baseUrl) {
                contentType = JSON
                content = objectMapper.writeValueAsString(timeTable)
            }
            // then
            patch.andDo { print() }
                .andExpect { status { isNotFound() } }
        }
    }

    @Nested
    @DisplayName("Delete Timetable | $baseUrl/{key}")
    @TestInstance(PER_CLASS)
    inner class DeleteTimeTable {
        @Test
        fun `should delete table of given name`() {
            // given
            val name = "test"
            // when/then
            mockMvc.delete("$baseUrl/$name")
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.name") { value(name) }
                }
            mockMvc.get("$baseUrl/$name")
                .andExpect { status { isNotFound() } }
        }

        @Test
        fun `should fail to delete nonexistent table`() {
            // given
            val name = "don't look for \"2c322eb1-8df2-4d3e-8369-686e49f76681\""
            // when/then
            mockMvc.delete("$baseUrl/$name")
                .andDo { print() }
                .andExpect { status { isNotFound() } }
        }
    }
}
