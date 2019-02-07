package ru.scratty.bot.notifications

import ru.scratty.mongo.DBService
import ru.scratty.utils.filterByCalendarAndLessonNumber
import java.util.*

class LessonNotification(time: Time,
                         private val numberLesson: Int,
                         private val send: (chatId: Long, text: String) -> Unit): Notification(time) {

    override fun execute() {
        val dbService = DBService.INSTANCE
        val users = dbService.getAllUsers()
        val calendar = GregorianCalendar()

        users.forEach { user ->
            if (user.settings[0] == '1') {
                val group = dbService.getGroup(user.groupId)

                dbService.getLessons(group.lessons)
                        .filterByCalendarAndLessonNumber(calendar, numberLesson)
                        .forEach {
                            val sb = StringBuilder()
                                    .appendln("Следующая пара:")
                                    .appendln(String.format("%s (%s/%s)", it.name, it.type, it.audience))

                            send(user.id, sb.toString())
                        }
            }
        }
    }
}