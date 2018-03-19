package ru.scratty.bot.commands

import org.telegram.telegrambots.api.objects.Update
import ru.scratty.db.Lesson
import ru.scratty.db.User
import java.util.*

class AddLessonCommand: Command("/пара|/урок".toRegex()) {

    override fun handleCommand(update: Update, user: User): String {
        if (db.getGroup(user.groupId).authorId != user._id) {
            return "У вас нет прав на редактирование рассписания этой группы"
        }

        val words = update.message.text.replace("/пара|/урок".toRegex(), "")

        if (words.isNotEmpty()) {
            val args = words.split(";")

            if (args.size == 6) {
                val lesson = Lesson()

                lesson.name = args[0]

                if (args[1].contains(",")) {
                    args[1].split(",").forEach {
                        lesson.weeks.add(it.trim().toInt())
                    }
                } else {
                    lesson.typeWeek = args[1].toInt()
                }

                lesson.dayNumber = getNumberOfDayOfWeek(args[2])
                lesson.number = args[3].toInt()
                lesson.audience = args[4]
                lesson.type = args[5]

                val group = db.getGroup(user.groupId)
                group.lessons.forEach {
                    if (it.weeks == lesson.weeks &&
                            it.typeWeek == lesson.typeWeek &&
                            it.dayNumber == lesson.dayNumber &&
                            it.number == lesson.number) {
                        group.lessons.remove(it)
                    }
                }

                group.lessons.add(lesson)
                db.updateGroup(group)

                return "Предмет успешно сохранен"
            }
        }
        return "Некорректное кол-во аргументов"
    }

    private fun getNumberOfDayOfWeek(dayName: String): Int {
        when {
            dayName.contains("пнд|пн|понедельник".toRegex()) -> return Calendar.MONDAY
            dayName.contains("вт|вторник".toRegex()) -> return Calendar.TUESDAY
            dayName.contains("ср|срд|среда".toRegex()) -> return Calendar.WEDNESDAY
            dayName.contains("чт|чет".toRegex()) -> return Calendar.THURSDAY
            dayName.contains("пт|пят".toRegex()) -> return Calendar.FRIDAY
            dayName.contains("сб|суббота".toRegex()) -> return Calendar.SATURDAY
        }

        return 0
    }

}