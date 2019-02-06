package ru.scratty.mongo.dao

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import com.mongodb.client.model.Updates.set
import org.bson.types.ObjectId
import ru.scratty.BotConfig
import ru.scratty.mongo.models.Lesson

class LessonsDAO(db: MongoDatabase): AbstractDAO<Lesson, ObjectId>(db, BotConfig.COL_LESSONS, Lesson::class) {

    override fun findOne(id: ObjectId): Lesson {
        val iter = collection.find(eq("_id", id))
        return iter.first() ?: Lesson()
    }

    override fun findAll() = collection.find().toList()

    override fun insert(obj: Lesson): ObjectId {
        collection.insertOne(obj)
        return obj.id
    }

    override fun update(obj: Lesson) {
        val updateFields = Updates.combine(
                set("name", obj.name),
                set("weeks", obj.weeks),
                set("typeWeek", obj.typeWeek),
                set("dayNumber", obj.dayNumber),
                set("number", obj.number),
                set("audience", obj.audience),
                set("type", obj.type))

        collection.updateOne(eq("_id", obj.id), updateFields)
    }

    override fun delete(obj: Lesson) {
        collection.deleteOne(eq("_id", obj.id))
    }

    fun findEqualLesson(lesson: Lesson): Lesson {
        val iter = collection.find(and(
                eq("name", lesson.name),
                eq("weeks", lesson.weeks),
                eq("typeWeek", lesson.typeWeek),
                eq("dayNumber", lesson.dayNumber),
                eq("number", lesson.number),
                eq("audience", lesson.audience),
                eq("type", lesson.type)))

        return iter.first() ?: Lesson()
    }
}