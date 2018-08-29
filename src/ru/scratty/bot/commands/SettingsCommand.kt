package ru.scratty.bot.commands

import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import ru.scratty.db.User

class SettingsCommand: Command("настройки|notification".toRegex()) {

    private var settings: String = ""

    override fun handleCommand(update: Update, user: User): String {
        if (isUpdate) {
            val num = text.replace("notification_", "").toInt()

            user.settings = user.settings.replaceRange(num, num + 1,
                    Math.abs(user.settings[num].toString().toInt() - 1).toString())
            db.updateUser(user)

            isUpdate = false
        }

        settings = user.settings
        val group = db.getGroup(user.groupId)

        val sb = StringBuilder()
        sb.appendln("Настройки")
                .appendln("Ваша группа: ${group.name}")
                .appendln("Уведомления перед парой " + (if (settings[0] == '1') "включены" else "выключены"))
                .appendln("Уведомления о сегодняшнем расписании " + (if (settings[1] == '1') "включены" else "выключены"))
                .appendln("Уведомления о завтрашнем расписании " + (if (settings[2] == '1') "включены" else "выключены"))

        return sb.toString()
    }

    override fun getKeyboard(): ReplyKeyboard {
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