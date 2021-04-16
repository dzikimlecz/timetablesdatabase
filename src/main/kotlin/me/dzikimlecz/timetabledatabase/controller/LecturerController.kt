package me.dzikimlecz.timetabledatabase.controller

import me.dzikimlecz.timetabledatabase.model.Lecturer
import me.dzikimlecz.timetabledatabase.service.LecturerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/lecturers")
class LecturerController(private val service: LecturerService) {
    @ExceptionHandler(NoSuchElementException::class)
    fun notFound(e: NoSuchElementException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.NOT_FOUND)

    @ExceptionHandler(IllegalArgumentException::class)
    fun badRequest(e: IllegalArgumentException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

    @GetMapping
    fun getLecturers() = service.getLecturers()

    @GetMapping("/{key}")
    fun getLecturer(@PathVariable key: String) = service.getLecturer(key)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun postLecturer(@RequestBody lecturer: Lecturer) = service.addLecturer(lecturer)

    @PatchMapping
    fun patchLecturer(@RequestBody lecturer: Lecturer) = service.patchLecturer(lecturer)

    @DeleteMapping("/{key}")
    fun deleteLecturer(@PathVariable key: String) = service.deleteLecturer(key)
}