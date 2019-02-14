package ru.scratty.mongo.dao

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import com.mongodb.client.model.Updates.set
import org.bson.types.ObjectId
import ru.scratty.BotConfig
import ru.scratty.mongo.models.Group

class GroupsDAO(db: MongoDatabase): AbstractDAO<Group, ObjectId>(db, BotConfig.COL_GROUPS, Group::class) {

    override fun findOne(id: ObjectId): Group {
        val iter = collection.find(eq("_id", id))
        return iter.first() ?: Group()
    }

    override fun findAll() = collection.find().toList()

    override fun insert(obj: Group): ObjectId {
        collection.insertOne(obj)
        return obj.id
    }

    override fun update(obj: Group) {
        val updateFields = Updates.combine(
                set("authorId", obj.authorId),
                set("name", obj.name),
                set("lessons", obj.lessons))

        collection.updateOne(Filters.eq("_id", obj.id), updateFields)
    }

    override fun delete(obj: Group) {
        collection.deleteOne(Filters.eq("_id", obj.id))
    }

    fun findOneByName(name: String): Group {
        val iter = collection.find(eq("name", name))
        return iter.first() ?: Group()
    }
}