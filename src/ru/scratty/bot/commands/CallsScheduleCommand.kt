package ru.scratty.bot.commands

import org.telegram.telegrambots.api.objects.Update
import ru.scratty.db.User

class CallsScheduleCommand: Command("звонки|звонок".toRegex()) {

    override fun handleCommand(update: Update, user: User): String {
        return "1 пара – 9:00-10:30\n" +
                "2 пара – 10:40-12:10\n" +
                "3 пара – 13:00-14:30\n" +
                "4 пара – 14:40-16:10\n" +
                "5 пара – 16:20-17:50\n" +
                "6 пара – 18:00-19:30"
    }
}