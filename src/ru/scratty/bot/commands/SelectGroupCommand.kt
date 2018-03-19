package ru.scratty.bot.commands

import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton
import ru.scratty.db.User

class SelectGroupCommand: Command("сменить группу|изменить группу|выбрать группу|group".toRegex()) {

    override fun handleCommand(update: Update, user: User): String {
        if (text.contains("groups"))
            isUpdate = false

        return if (isUpdate) {
            val newGroupId = update.callbackQuery.data.replace("group_", "").toInt()
            user.groupId = newGroupId
            db.updateUser(user)


            "Группа успешно установлена"
        } else {
            "Выберите вашу группу:"
        }
    }

    override fun getKeyboard(): ReplyKeyboard {
        if (isUpdate)
            return InlineKeyboardMarkup()


        val keyboardMarkup = InlineKeyboardMarkup()
        val rows = ArrayList<ArrayList<InlineKeyboardButton>>()

        db.getGroups().forEach {
            rows.add(arrayListOf(InlineKeyboardButton(it.name).setCallbackData("group_" + it._id)))
        }

        keyboardMarkup.keyboard = rows as List<MutableList<InlineKeyboardButton>>?

        return keyboardMarkup
    }
}