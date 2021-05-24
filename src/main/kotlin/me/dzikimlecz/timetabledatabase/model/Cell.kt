package me.dzikimlecz.timetabledatabase.model

import java.util.concurrent.atomic.AtomicInteger
import javax.persistence.Entity

data class Cell(
    val content: List<String>,
    val divisionOrientation: DivisionDirection?,
): Iterable<String> {

    operator fun get(i: Int): String =
        if (i > 0 && divisionOrientation === null)
            throw IndexOutOfBoundsException("Cell has only one content field. Attempted to access field: $i")
        else content[i]


    override fun iterator(): Iterator<String> = this.CellIterator()

    private inner class CellIterator: Iterator<String> {
        val index = AtomicInteger(-1)

        override fun hasNext(): Boolean =
            if (index.get() < 0) true
            else divisionOrientation !== null && index.get() < 1

        override fun next(): String =
            if (hasNext()) this@Cell[index.incrementAndGet()]
            else throw NoSuchElementException()
    }
}

enum class DivisionDirection {
    VERTICAL, HORIZONTAL;

    companion object {
        fun of(value: String?): DivisionDirection? =
            if (value !== null) valueOf(value.uppercase()) else null
    }
}