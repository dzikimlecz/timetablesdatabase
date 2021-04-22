package me.dzikimlecz.timetabledatabase.model.database

interface DataSource<E> {
    fun retrieve(): Collection<E>

    fun retrieve(key: String): E

    fun create(e: E): E

    fun patch(e: E): E
    fun delete(key: String): E

}