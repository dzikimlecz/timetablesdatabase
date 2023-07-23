package me.dzikimlecz.timetabledatabase.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dzikimlecz.lecturers.Lecturer
import me.dzikimlecz.lecturers.SettlingPeriod
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.*
import java.time.LocalDate

private const val baseUrl = "/timetableapi/lecturers"

private const val key = "MN"

private val l1 = Lecturer("${key[0]}an ${key[1]}an", hoursWorked = mapOf())
private val l2 = Lecturer("Posted Guy", hoursWorked = mapOf())

@SpringBootTest
@AutoConfigureMockMvc
internal class LecturerControllerTest @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper
) {

    @BeforeEach
    fun setUp() {
        mockMvc.post(baseUrl) {
            contentType = APPLICATION_JSON
            content = objectMapper.writeValueAsString(l1)
        }.andReturn()
    }

    @AfterEach
    fun tearDown() {
        mockMvc.delete("$baseUrl/${l1.code}").andReturn()
        mockMvc.delete("$baseUrl/${l2.code}").andReturn()
    }

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
                    jsonPath("$[0].name") { exists() }
                }
        }
    }

    @Nested
    @DisplayName("Get Lecturer")
    @TestInstance(PER_CLASS)
    inner class GetLecturer {
        @Test
        fun `should return lecturer of given key`() {
            mockMvc.get("$baseUrl/$key")
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(APPLICATION_JSON) }
                    jsonPath("$.name") { value(l1.name) }
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
            val posted = l2
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
            val posted = l1
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
            val patched = Lecturer(l1.name, l1.code,
                mapOf(SettlingPeriod(LocalDate.now(), LocalDate.now().plusYears(20)) to 0)
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
                mapOf(SettlingPeriod(LocalDate.now(), LocalDate.now().plusYears(20)) to 0)
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
            val deleted = l1
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