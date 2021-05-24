package me.dzikimlecz.timetabledatabase.model.database

import org.springframework.data.mongodb.repository.MongoRepository

interface DataSourceOf<E: Any>: MongoRepository<E, String> {

    /**
     * Returns all instances of E found in the database
     */
    fun retrieve(): Collection<E> = findAll()

    /**
     * Returns instance of E of id identical to the given one. If nothing matches, throws [NoSuchElementException]
     */
    fun retrieve(key: String): E

    /**
     * Creates new record in the db from the given object. Throws [IllegalArgumentException],
     * if any records of that id already exist
     */
    fun create(e: E): E

    /**
     * Updates record of the id equal to the one of the passed object. If there's no record of that id,
     * throws [NoSuchElementException]
     */
    fun patch(e: E): E

    /**
     * Deletes record of the given id from the database. Throws [NoSuchElementException], if no record of that id exists.
     */
    fun delete(key: String): E {
        val deleted = retrieve(key)
        delete(deleted)
        return deleted
    }

}