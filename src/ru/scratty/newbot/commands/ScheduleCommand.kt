package ru.scratty.newbot.commands

import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.scratty.ScheduleConstructor
import ru.scratty.mongo.DBService
import ru.scratty.newbot.extensions.*
import java.util.*

class ScheduleCommand(private val dbService: DBService):
        Command(Regex("день|сегодня|завтра|day|неделя|week")),
        ISimpleCommand,
        ICallbackCommand {


    override fun handleMessage(sender: AbsSender, message: Message) {
        val user = dbService.getUser(message.chatId)
        if (user.groupId.isEmpty()) {
            sendMessage(sender, message.chatId, "Вы не выбрали группу")
            return
        }

        val group = dbService.getGroup(user.groupId)
        val lessons = dbService.getLessons(group.lessons)

        val scheduleConstructor = ScheduleConstructor(lessons)
        val range = message.text.toLowerCase().trim()

        val text: String
        val prefix: String
        val increment: Int

        when {
            range.contains("день|сегодня|day".toRegex()) -> {
                text = scheduleConstructor.getTodaySchedule()
                prefix = "day"
                increment = getDayNumber()
            }
            range.contains("завтра".toRegex()) -> {
                text = scheduleConstructor.getTomorrowSchedule()
                prefix = "day"
                increment = getDayNumber() + 1
            }
            else -> {
                text = scheduleConstructor.getWeekSchedule(0)
                prefix = "week"
                increment = 0
            }
        }

        sendMessage(sender, message.chatId, text, getKeyboard(prefix, increment))
    }

    override fun handleCallbackMessage(sender: AbsSender, callbackQuery: CallbackQuery) {
        val user = dbService.getUser(callbackQuery.message.chatId)
        val group = dbService.getGroup(user.groupId)
        val lessons = dbService.getLessons(group.lessons)

        val scheduleConstructor = ScheduleConstructor(lessons)

        val words = callbackQuery.data.split('_')
        val prefix = words[0]
        val increment = words[1].toInt()

        val schedule = if (prefix == "day") {
            scheduleConstructor.getDaySchedule(increment)
        } else {
            scheduleConstructor.getWeekSchedule(increment)
        }

        updateMessage(sender, callbackQuery.message, schedule, getKeyboard(prefix, increment))
    }

    private fun getKeyboard(prefix: String, increment: Int): InlineKeyboardMarkup {
        val keyboardMarkup = InlineKeyboardMarkup()
        val rows = ArrayList<ArrayList<InlineKeyboardButton>>()
        val row = ArrayList<InlineKeyboardButton>()

        val prev = increment - 1
        row.add(InlineKeyboardButton("<-").setCallbackData("${prefix}_$prev"))
        val next = increment + 1
        row.add(InlineKeyboardButton("->").setCallbackData("${prefix}_$next"))
        rows.add(row)

        keyboardMarkup.keyboard = rows as List<MutableList<InlineKeyboardButton>>?

        return keyboardMarkup
    }

    private fun getDayNumber(): Int = GregorianCalendar().get(Calendar.DAY_OF_YEAR)
}