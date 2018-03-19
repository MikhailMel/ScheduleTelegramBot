package ru.scratty.bot.commands

import org.telegram.telegrambots.api.objects.Update
import ru.scratty.db.User
import ru.scratty.utils.ScheduleConstructor

class DayScheduleCommand : Command("день|сегодня|завтра".toRegex()) {

    override fun handleCommand(update: Update, user: User): String {
        val group = db.getGroup(user.groupId)

        return if (update.message.text.toLowerCase().contains("завтра"))
            ScheduleConstructor(group).getTomorrowSchedule()
        else
            ScheduleConstructor(group).getTodaySchedule()
    }
}