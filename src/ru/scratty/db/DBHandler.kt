package ru.scratty.db

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import org.litote.kmongo.updateOne
import ru.scratty.utils.Config
import java.util.*
import java.util.Calendar.DAY_OF_WEEK
import kotlin.collections.ArrayList

class DBHandler private constructor() {

    private object Holder {
        val INSTANCE = DBHandler()
    }

    companion object {
        val INSTANCE: DBHandler by lazy { Holder.INSTANCE }

        private lateinit var users: MongoCollection<User>
        private lateinit var groups: MongoCollection<Group>
    }

    init {
        val client = KMongo.createClient()
        val db = client.getDatabase("schedule_telegram_bot")

        users = db.getCollection<User>("users")
        groups = db.getCollection<Group>("groups")
    }

    fun userIsExists(userId: Long): Boolean {
        return users.count() > 0 && getUser(userId)._id != 0L
    }

    fun getUser(userId: Long): User {
        val user = users.find(Filters.eq("_id", userId))

        return if (user.count() > 0) user.first() else User()
    }

    fun getUsers(): ArrayList<User> {
        val arr = ArrayList<User>()

        users.find().forEach {
            arr.add(it)
        }

        return arr
    }

    fun addUser(userId: Long, name: String) {
        users.insertOne(User(userId, name))
    }

    fun updateUser(user: User) {
        users.updateOne(user)
    }

    fun getGroup(groupId: Int): Group {
        val group = groups.find(Filters.eq("_id", groupId))

        return if (group.count() > 0) group.first() else Group()
    }

    fun getGroups(): ArrayList<Group> {
        val arr = ArrayList<Group>()

        groups.find().forEach {
            arr.add(it)
        }

        return arr
    }

    fun addGroup(group: Group) {
        groups.insertOne(group)
    }

    fun countGroups(): Int {
        return groups.count().toInt()
    }

    fun updateGroup(group: Group) {
        groups.updateOne(group)
    }
}

data class User(@BsonId var _id: Long = 0L,
                var name: String = "",
                var username: String = "",
                var groupId: Int = 0,
                var settings: String = "111")

data class Group(@BsonId var _id: Int = 0,
                 var authorId: Long = 0L,
                 var name: String = "",
                 var lessons: ArrayList<Lesson> = ArrayList())

data class Lesson(var name: String = "",
                  var weeks: ArrayList<Int> = ArrayList(),
                  var typeWeek: Int = 0,
                  var dayNumber: Int = 0,
                  var number: Int = 0,
                  var audience: String = "",
                  var type: String = "") {

    fun check(calendar: Calendar, number: Int): Boolean {
        if (number == this.number && calendar.get(DAY_OF_WEEK) == dayNumber) {
            if (weeks.size == 0) {
                if ((calendar.get(Calendar.WEEK_OF_YEAR) - Config.WEEKS_SHIFT) % 2 == typeWeek % 2) {
                    return true
                }
            } else {
                return weeks.any { (calendar.get(Calendar.WEEK_OF_YEAR) - Config.WEEKS_SHIFT) == it }
            }
        }

        return false
    }
}