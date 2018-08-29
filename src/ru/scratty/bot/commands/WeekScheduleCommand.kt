package ru.scratty.bot.commands

import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import ru.scratty.db.User
import ru.scratty.utils.ScheduleConstructor

class WeekScheduleCommand: Command("неделя|week".toRegex()) {

    private var incrementWeek = 0

    override fun handleCommand(update: Update, user: User): String {
        return if (isUpdate) {
            incrementWeek = update.callbackQuery.data.replace("неделя_|week_".toRegex(), "").toInt()

            ScheduleConstructor(db.getGroup(user.groupId)).getWeekSchedule(incrementWeek)
        } else {
            incrementWeek = 0

            ScheduleConstructor(db.getGroup(user.groupId)).getWeekSchedule(0)
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