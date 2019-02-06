package ru.scratty.bot.notifications

import ru.scratty.mongo.DBService
import java.util.*

class LessonNotification(time: Time, private val numberLesson: Int,
                         private val send: (chatId: Long, text: String) -> Unit): Notification(time) {

    override fun execute() {
        val dbService = DBService.INSTANCE
        val users = dbService.getAllUsers()
        val calendar = GregorianCalendar()

        users.forEach { user ->
            if (user.settings[0] == '1') {
                val group = dbService.getGroup(user.groupId)

                val lessons = dbService.getLessons(group.lessons)
                lessons.forEach { lesson ->
                    if (lesson.check(calendar, numberLesson)) {
                        val sb = StringBuilder()
                                .appendln("Следующая пара:")
                                .appendln(String.format("%s (%s/%s)", lesson.name, lesson.type, lesson.audience))

                        send(user.id, sb.toString())
                    }
                }
            }
        }
    }
}