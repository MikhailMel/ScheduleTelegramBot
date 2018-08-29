package ru.scratty.db

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import org.litote.kmongo.updateOne

class DBHandlerMongo : DBHandler {

    private object Holder {
        val INSTANCE = DBHandlerMongo()
    }

    companion object {
        val INSTANCE: DBHandlerMongo by lazy { Holder.INSTANCE }

        private lateinit var users: MongoCollection<User>
        private lateinit var groups: MongoCollection<Group>
    }

    init {
        val client = KMongo.createClient()
        val db = client.getDatabase("schedule_telegram_bot")

        users = db.getCollection<User>("users")
        groups = db.getCollection<Group>("groups")
    }

    override fun userIsExists(userId: Long): Boolean {
        return users.count() > 0 && getUser(userId)._id != 0L
    }

    override fun getUser(userId: Long): User {
        val user = users.find(Filters.eq("_id", userId))

        return if (user.count() > 0) user.first() else User()
    }

    override fun getUsers(): ArrayList<User> {
        val arr = ArrayList<User>()

        users.find().forEach {
            arr.add(it)
        }

        return arr
    }

    override fun addUser(userId: Long, name: String) {
        users.insertOne(User(userId, name))
    }

    override fun updateUser(user: User) {
        users.updateOne(user)
    }

    override fun getGroup(groupId: Int): Group {
        val group = groups.find(Filters.eq("_id", groupId))

        return if (group.count() > 0) group.first() else Group()
    }

    override fun getGroups(): ArrayList<Group> {
        val arr = ArrayList<Group>()

        groups.find().forEach {
            arr.add(it)
        }

        return arr
    }

    override fun addGroup(group: Group) {
        groups.insertOne(group)
    }

    override fun countGroups(): Int {
        return groups.count().toInt()
    }

    override fun updateGroup(group: Group) {
        groups.updateOne(group)
    }

}