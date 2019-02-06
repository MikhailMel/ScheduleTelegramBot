package ru.scratty.mongo.dao

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates.combine
import com.mongodb.client.model.Updates.set
import ru.scratty.BotConfig
import ru.scratty.mongo.models.User

class UsersDAO(db: MongoDatabase): AbstractDAO<User, Long>(db, BotConfig.COL_USERS, User::class) {

    override fun findOne(id: Long): User {
        val iter = collection.find(eq("_id", id))
        return iter.first() ?: User()
    }

    override fun findAll() = collection.find().toList()

    override fun insert(obj: User): Long {
        collection.insertOne(obj)
        return obj.id
    }

    override fun update(obj: User) {
        val updateFields = combine(
                set("name", obj.name),
                set("username", obj.username),
                set("groupId", obj.groupId),
                set("settings", obj.settings))

        collection.updateOne(eq("_id", obj.id), updateFields)
    }

    override fun delete(obj: User) {
        collection.deleteOne(eq("_id", obj.id))
    }
}