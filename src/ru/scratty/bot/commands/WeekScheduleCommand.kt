package ru.scratty.bot.commands

import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import ru.scratty.ScheduleConstructor
import ru.scratty.mongo.models.User

class WeekScheduleCommand: Command("неделя|week".toRegex()) {

    private var incrementWeek = 0

    override fun handleCommand(update: Update, user: User): String {
        return if (isUpdate) {
            incrementWeek = update.callbackQuery.data.replace("неделя_|week_".toRegex(), "").toInt()

            if (user.groupId.isNotEmpty()) {
                val lessons = dbService.getLessons(dbService.getGroup(user.groupId).lessons)
                ScheduleConstructor(lessons).getWeekSchedule(incrementWeek)
            } else {
                "Сначала нужно выбрать группу"
            }
        } else {
            incrementWeek = 0

            if (user.groupId.isNotEmpty()) {
                val lessons = dbService.getLessons(dbService.getGroup(user.groupId).lessons)
                ScheduleConstructor(lessons).getWeekSchedule(0)
            } else {
                "Сначала нужно выбрать группу"
            }
        }
    }

    override fun getKeyboard(): ReplyKeyboard {
        val keyboardMarkup = InlineKeyboardMarkup()
        val rows = ArrayList<ArrayList<InlineKeyboardButton>>()
        val row = ArrayList<InlineKeyboardButton>()

        val prev = incrementWeek - 1
        row.add(InlineKeyboardButton("<-").setCallbackData("week_$prev"))
        val next = incrementWeek + 1
        row.add(InlineKeyboardButton("->").setCallbackData("week_$next"))
        rows.add(row)

        keyboardMarkup.keyboard = rows as List<MutableList<InlineKeyboardButton>>?

        return keyboardMarkup
    }
}