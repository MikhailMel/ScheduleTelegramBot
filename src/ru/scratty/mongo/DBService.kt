package ru.scratty.mongo

import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.ServerAddress
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import org.bson.types.ObjectId
import ru.scratty.BotConfig
import ru.scratty.BotConfig.DB_HOST
import ru.scratty.BotConfig.DB_PORT
import ru.scratty.mongo.dao.GroupsDAO
import ru.scratty.mongo.dao.LessonsDAO
import ru.scratty.mongo.dao.UsersDAO
import ru.scratty.mongo.models.Group
import ru.scratty.mongo.models.Lesson
import ru.scratty.mongo.models.User

class DBService private constructor() {

    private object Holder {
        val INSTANCE = DBService()
    }

    companion object {
        val INSTANCE: DBService by lazy { Holder.INSTANCE }
    }

    private val usersDAO: UsersDAO
    private val groupsDAO: GroupsDAO
    private val lessonsDAO: LessonsDAO

    init {
        val pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()))

        val client = MongoClient(
                ServerAddress(DB_HOST, DB_PORT),
//                MongoCredential.createCredential(DB_USERNAME, "admin", DB_PASSWORD.toCharArray()),
                MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build())

        val db = client.getDatabase(BotConfig.DB_DATABASE)

        usersDAO = UsersDAO(db)
        groupsDAO = GroupsDAO(db)
        lessonsDAO = LessonsDAO(db)
    }

    fun addUser(user: User): Long {
        if (userIsExists(user.id)) {
            return user.id
        }

        return usersDAO.insert(user)
    }

    fun userIsExists(id: Long) = usersDAO.findOne(id).id != -1L

    fun getUser(id: Long) = usersDAO.findOne(id)

    fun getAllUsers() = usersDAO.findAll()

    fun editUser(user: User) {
        usersDAO.update(user)
    }

    fun addGroup(group: Group): String {
        val foundGroup = groupsDAO.findOneByName(group.name)
        if (foundGroup.name.isNotEmpty()) {
            return foundGroup.id.toHexString()
        }

        return groupsDAO.insert(group).toHexString()
    }

    fun getGroup(id: String) = groupsDAO.findOne(ObjectId(id))

    fun getAllGroups() = groupsDAO.findAll()

    fun editGroup(group: Group) {
        groupsDAO.update(group)
    }

    fun addLesson(lesson: Lesson): String {
        val foundLesson = lessonsDAO.findEqualLesson(lesson)
        if (foundLesson.name.isNotEmpty()) {
            return foundLesson.id.toHexString()
        }

        return lessonsDAO.insert(lesson).toHexString()
    }

    fun getLessons(ids: List<String>) = lessonsDAO.findAll().filter { ids.contains(it.id.toHexString()) }

    fun getLesson(id: String) = lessonsDAO.findOne(ObjectId(id))
}