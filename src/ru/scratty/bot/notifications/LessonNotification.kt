package ru.scratty.bot.notifications

import ru.scratty.db.DBHandlerMongo
import java.util.*

class LessonNotification(time: Time, private val numberLesson: Int,
                         private val send: (chatId: Long, text: String) -> Unit): Notification(time) {

    override fun execute() {
        val db = DBHandlerMongo.INSTANCE
        val users = db.getUsers()
        val calendar = GregorianCalendar()

        users.forEach {
            if (it.settings[0] == '1') {
                val group = db.getGroup(it.groupId)
                val user = it

                group.lessons.forEach {
                    if (it.check(calendar, numberLesson)) {
                        val sb = StringBuilder()
                                .appendln("Следующая пара:")
                                .appendln(String.format("%s (%s/%s)", it.name, it.type, it.audience))

                        send(user._id, sb.toString())
                    }
                }
            }
        }
    }
}