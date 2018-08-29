package ru.scratty.db

import org.bson.codecs.pojo.annotations.BsonId
import ru.scratty.utils.Config
import java.util.*
import java.util.Calendar.DAY_OF_WEEK
import kotlin.collections.ArrayList

interface DBHandler {


    fun userIsExists(userId: Long): Boolean
    fun getUser(userId: Long): User
    fun getUsers(): ArrayList<User>
    fun addUser(userId: Long, name: String)
    fun updateUser(user: User)

    fun getGroup(groupId: Int): Group
    fun getGroups(): ArrayList<Group>
    fun addGroup(group: Group)
    fun countGroups(): Int
    fun updateGroup(group: Group)
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