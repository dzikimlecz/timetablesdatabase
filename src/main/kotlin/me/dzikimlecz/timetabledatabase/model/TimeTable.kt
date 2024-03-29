package me.dzikimlecz.timetabledatabase.model

import com.fasterxml.jackson.annotation.JsonIgnore
import me.dzikimlecz.timetables.TimeSpan
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document
data class TimeTable(
    val date: LocalDate,
    val name: String,
    val table: List<List<Cell>>,
    val timeSpans: List<List<TimeSpan?>>,
    @JsonIgnore @Id var id: ObjectId? = null,
)
