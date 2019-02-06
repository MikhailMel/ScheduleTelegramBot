package ru.scratty.mongo.dao

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import kotlin.reflect.KClass

abstract class AbstractDAO<T: Any, I>(db: MongoDatabase,
                                      collectionName: String,
                                      clazz: KClass<T>) {

    protected val collection: MongoCollection<T> = db.getCollection(collectionName, clazz.java)

    abstract fun findOne(id: I): T
    abstract fun findAll(): List<T>
    abstract fun insert(obj: T): I
    abstract fun update(obj: T)
    abstract fun delete(obj: T)

}