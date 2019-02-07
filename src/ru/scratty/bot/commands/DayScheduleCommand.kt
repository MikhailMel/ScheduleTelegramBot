package ru.scratty.bot.commands

import org.telegram.telegrambots.meta.api.objects.Update
import ru.scratty.mongo.models.User
import ru.scratty.ScheduleConstructor

class DayScheduleCommand : Command("день|сегодня|завтра".toRegex()) {

    override fun handleCommand(update: Update, user: User): String {
        if (user.groupId.isEmpty()) {
            return "Сначала нужно выбрать группу"
        }

        val group = dbService.getGroup(user.groupId)
        val lessons = dbService.getLessons(group.lessons)

        return if (update.message.text.toLowerCase().contains("завтра"))
            ScheduleConstructor(lessons).getTomorrowSchedule()
        else
            ScheduleConstructor(lessons).getTodaySchedule()
    }
}