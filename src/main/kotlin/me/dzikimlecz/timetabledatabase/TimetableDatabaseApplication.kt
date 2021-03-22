package me.dzikimlecz.timetabledatabase

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TimetableDatabaseApplication

fun main(args: Array<String>) {
	runApplication<TimetableDatabaseApplication>(*args)
}
