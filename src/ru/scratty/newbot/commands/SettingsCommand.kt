package ru.scratty.newbot.commands

import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.scratty.mongo.DBService
import ru.scratty.mongo.models.Group
import ru.scratty.mongo.models.User
import ru.scratty.newbot.extensions.*

class SettingsCommand(private val dbService: DBService) : Command(Regex("настройки|notification")),
        ISimpleCommand,
        ICallbackCommand {


    override fun handleMessage(sender: AbsSender, message: Message) {
        val user = dbService.getUser(message.chatId)
        val group = dbService.getGroup(user.groupId)

        sendMessage(sender, message.chatId, createMsg(user, group), getKeyboard(user.settings))
    }

    override fun handleCallbackMessage(sender: AbsSender, callbackQuery: CallbackQuery) {
        val user = dbService.getUser(callbackQuery.message.chatId)
        val group = dbService.getGroup(user.groupId)

        val notificationNumber = callbackQuery.data.replace("notification_", "").toInt()

        user.settings = user.settings.replaceRange(notificationNumber, notificationNumber + 1,
                Math.abs(user.settings[notificationNumber].toString().toInt() - 1).toString())
        dbService.editUser(user)

        updateMessage(sender, callbackQuery.message, createMsg(user, group), getKeyboard(user.settings))
    }

    private fun createMsg(user: User, group: Group) = StringBuilder().apply {
        appendln("Настройки")
        appendln("Ваша группа: ${group.name}")
        appendln("Уведомления перед парой " + (if (user.settings[0] == '1') "включены" else "выключены"))
        appendln("Уведомления о сегодняшнем расписании " + (if (user.settings[1] == '1') "включены" else "выключены"))
        appendln("Уведомления о завтрашнем расписании " + (if (user.settings[2] == '1') "включены" else "выключены"))
    }.toString()


    private fun getKeyboard(settings: String): ReplyKeyboard {
        val keyboardMarkup = InlineKeyboardMarkup()
        val rows = ArrayList<ArrayList<InlineKeyboardButton>>()

        rows.add(arrayListOf(InlineKeyboardButton("Изменить группу").setCallbackData("groups")))
        rows.add(arrayListOf(InlineKeyboardButton((if (settings[0] == '1') "Выключить" else "Включить") + " уведомления перед парами").setCallbackData("notification_0")))
        rows.add(arrayListOf(InlineKeyboardButton((if (settings[1] == '1') "Выключить" else "Включить") + " уведомления о сегодняшнем расписании").setCallbackData("notification_1")))
        rows.add(arrayListOf(InlineKeyboardButton((if (settings[2] == '1') "Выключить" else "Включить") + " уведомления о завтрашнем расписании").setCallbackData("notification_2")))

        keyboardMarkup.keyboard = rows as List<MutableList<InlineKeyboardButton>>?

        return keyboardMarkup
    }
}