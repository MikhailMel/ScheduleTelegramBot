package ru.scratty.bot.commands

import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import ru.scratty.mongo.models.User

class SelectGroupCommand: Command("сменить группу|изменить группу|выбрать группу|group".toRegex()) {

    override fun handleCommand(update: Update, user: User): String {
        if (text.contains("groups"))
            isUpdate = false

        return if (isUpdate) {
            val newGroupId = update.callbackQuery.data.replace("group_", "")
            user.groupId = newGroupId
            dbService.editUser(user)


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

        dbService.getAllGroups().forEach {
            rows.add(arrayListOf(InlineKeyboardButton(it.name).setCallbackData("group_" + it.id)))
        }

        keyboardMarkup.keyboard = rows as List<MutableList<InlineKeyboardButton>>?

        return keyboardMarkup
    }
}