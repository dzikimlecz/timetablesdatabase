package me.dzikimlecz.timetabledatabase.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dzikimlecz.timetabledatabase.model.Lecturer
import me.dzikimlecz.timetabledatabase.model.SettlingPeriod
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.*
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
internal class LecturerControllerTest @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper
) {

    val baseUrl = "/api/lecturers"


    @Nested
    @DisplayName("Get Lecturers")
    @TestInstance(PER_CLASS)
    inner class GetLecturers {
        @Test
        fun `should return all lecturers`() {
            mockMvc.get(baseUrl)
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(APPLICATION_JSON) }
                    jsonPath("$[0].name") { value("Marcin Najman") }
                }
        }
    }

    @Nested
    @DisplayName("Get Lecturer")
    @TestInstance(PER_CLASS)
    inner class GetLecturer {
        @Test
        fun `should return lecturer of given key`() {
            //given
            val key = "MN"
            //when/then
            mockMvc.get("$baseUrl/$key")
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(APPLICATION_JSON) }
                    jsonPath("$.name") { value("Marcin Najman") }
                }
        }

        @Test
        fun `should return NOT FOUND if key is invalid`() {
            // given
            val key = "Not findable Key"
            // when/then
            mockMvc.get("$baseUrl/$key")
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                }
        }
    }
    
    @Nested
    @DisplayName("Post Lecturer")
    @TestInstance(PER_CLASS)
    inner class AddLecturer {
        @Test
        fun `should post a given Lecturer object`() {
            // given
            val posted = Lecturer("Posted Guy", hoursWorkedStrings = mapOf())
            // when
            val post = mockMvc.post(baseUrl) {
                contentType = APPLICATION_JSON
                content = objectMapper.writeValueAsString(posted)
            }
            // then
            post.andDo { print() }
                .andExpect {
                    status { isCreated() }
                    content { contentType(APPLICATION_JSON) }
                    jsonPath("$.name") { value(posted.name ) }
                }

        }

        @Test
        fun `should fail to add Lecturer of already existing code and return Bad Request`() {
            // given
            val posted = Lecturer("Marcin Najman", hoursWorkedStrings = emptyMap())
            // when
            val post = mockMvc.post(baseUrl) {
                contentType = APPLICATION_JSON
                content = objectMapper.writeValueAsString(posted)
            }
            // then
            post.andDo { print() }
                .andExpect { status { isBadRequest() } }
        }
    }
    
    @Nested
    @DisplayName("Patch Lecturer")
    @TestInstance(PER_CLASS)
    inner class PatchLecturer {
        @Test
        fun `should patch an existing lecturer`() {
            // given
            val patched = Lecturer("Jeff Bezos", "JB",
                mapOf(SettlingPeriod(LocalDate.now(), LocalDate.now().plusYears(20)).toString() to 0)
            )
            // when
            val patch = mockMvc.patch(baseUrl) {
                contentType = APPLICATION_JSON
                content = objectMapper.writeValueAsString(patched)}
            // then
            patch.andDo { print() }
                .andExpect {
                    status { isOk() }
                    content {
                        contentType(APPLICATION_JSON)
                        json(objectMapper.writeValueAsString(patched))
                    }
                }
            mockMvc.get("$baseUrl/${patched.code}")
                .andExpect { content { json(objectMapper.writeValueAsString(patched)) } }
        }

        @Test
        fun `should fail to patch nonexistent Lecturer`() {
            // given
            val patched = Lecturer("Non Existent", "NON_EXISTENT",
                mapOf(SettlingPeriod(LocalDate.now(), LocalDate.now().plusYears(20)).toString() to 0)
            )
            // when
            val patch = mockMvc.patch(baseUrl) {
                contentType = APPLICATION_JSON
                content = objectMapper.writeValueAsString(patched)
            }
            // then
            patch.andExpect { status { isNotFound() } }
        }
    }
    
    
    @Nested
    @DisplayName("Delete Lecturer")
    @TestInstance(PER_CLASS)
    inner class DeleteLecturer {
        @Test
        fun `should delete lecturer of given code`() {
            // given
            val deleted = Lecturer("Marcin Najman", "MN", emptyMap())
            // when
            val delete = mockMvc.delete("$baseUrl/${deleted.code}")
            // then
            delete.andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { json(objectMapper.writeValueAsString(deleted)) }
                }
            mockMvc.get("$baseUrl/${deleted.code}")
                .andExpect { status { isNotFound() } }
        }

        @Test
        fun `should get 404 on when trying to delete nonexistent lecturer`() {
            // given
            val nonExistentCode = "you ain't gonna find me"
            // when/ then
            mockMvc.delete("$baseUrl/$nonExistentCode").andExpect { status { isNotFound() } }
        }
    }
}