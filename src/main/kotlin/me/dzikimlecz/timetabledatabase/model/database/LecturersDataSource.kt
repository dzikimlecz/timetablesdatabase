package me.dzikimlecz.timetabledatabase.model.database

import me.dzikimlecz.timetabledatabase.model.Lecturer

interface LecturersDataSource : DataSource<Lecturer> {

    fun retrieve(predicate: (Lecturer) -> Boolean) = retrieve().filter(predicate)

    override fun retrieve(key: String): Lecturer {
        if (key.count { it.isWhitespace() } > 2) try {
            return retrieve().first { it.name.equals(key, ignoreCase = true) }
        } catch(_: NoSuchElementException) {}
        return retrieve().firstOrNull { it.code.equals(key, ignoreCase = true) }
            ?: throw NoSuchElementException("Could not find lecturer $key")
    }

}