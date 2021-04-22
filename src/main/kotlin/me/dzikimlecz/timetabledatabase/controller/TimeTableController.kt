package me.dzikimlecz.timetabledatabase.controller

import me.dzikimlecz.timetabledatabase.model.TimeTable
import me.dzikimlecz.timetabledatabase.service.TimeTableService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/timetableapi/timetables")
class TimeTableController(val service: TimeTableService) {

    @ExceptionHandler(NoSuchElementException::class)
    fun notFound(e: NoSuchElementException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.NOT_FOUND)

    @ExceptionHandler(IllegalArgumentException::class)
    fun badRequest(e: IllegalArgumentException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

    @GetMapping
    fun getLecturers() =
        service.getTimeTables()

    @GetMapping("/{key}")
    fun getLecturer(@PathVariable key: String) =
        service.getTimeTable(key)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun postLecturer(@RequestBody table: TimeTable) =
        service.addTimeTable(table)

    @PatchMapping
    fun patchLecturer(@RequestBody table: TimeTable) =
        service.patchTimeTable(table)

    @DeleteMapping("/{key}")
    fun deleteLecturer(@PathVariable key: String) =
        service.deleteTimeTable(key)
}