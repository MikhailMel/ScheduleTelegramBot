package ru.scratty.bot.commands

import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import ru.scratty.mongo.DBService
import ru.scratty.mongo.models.User

abstract class Command(private val containsWords: Regex) {

    protected val dbService = DBService.INSTANCE
    protected var text: String = ""

    var isUpdate = false
    var isSendMessage = false

    fun checkCommand(update: Update): Boolean {
        if (update.hasMessage() && update.message.hasText()) {
            isUpdate = false

            text = update.message.text.toLowerCase()
            return text.contains(containsWords)
        } else if (update.hasCallbackQuery()) {
            isUpdate = true

            text = update.callbackQuery.data
            return text.contains(containsWords)
        }

        return false
    }

    abstract fun handleCommand(update: Update, user: User): String

    open fun getMessageForSendPair(): Pair<Long, String> {
        return Pair(0, "")
    }

    open fun getKeyboard(): ReplyKeyboard {
        val keyboardMarkup = ReplyKeyboardMarkup()
        val keyboard = ArrayList<KeyboardRow>()

        var row = KeyboardRow()
        row.add("Сегодня")
        row.add("Завтра")
        row.add("Неделя")
        keyboard.add(row)


        row = KeyboardRow()
        row.add("Настройки")
        row.add("Звонки")
        keyboard.add(row)
        keyboardMarkup.keyboard = keyboard
        keyboardMarkup.resizeKeyboard = true

        return keyboardMarkup
    }
}