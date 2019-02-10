package ru.scratty.newbot.commands

import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.scratty.mongo.DBService
import ru.scratty.newbot.extensions.*

class SelectGroupCommand(private val dbService: DBService):
        Command(Regex("сменить группу|изменить группу|выбрать группу|group")),
        ISimpleCommand,
        ICallbackCommand {


    override fun handleMessage(sender: AbsSender, message: Message) {
        sendMessage(sender, message.chatId, "Выберите вашу группу:", getKeyboard())
    }

    override fun handleCallbackMessage(sender: AbsSender, callbackQuery: CallbackQuery) {
        val newGroupId = callbackQuery.data.replace("group_", "")
        val user = dbService.getUser(callbackQuery.message.chatId)

        user.groupId = newGroupId
        dbService.editUser(user)

        updateMessage(sender, callbackQuery.message, "Группа успешно установлена")
    }


    fun getKeyboard(): ReplyKeyboard {
        val keyboardMarkup = InlineKeyboardMarkup()
        val rows = ArrayList<ArrayList<InlineKeyboardButton>>()

        dbService.getAllGroups().forEach {
            rows.add(arrayListOf(InlineKeyboardButton(it.name).setCallbackData("group_" + it.id)))
        }

        keyboardMarkup.keyboard = rows as List<MutableList<InlineKeyboardButton>>?

        return keyboardMarkup
    }
}