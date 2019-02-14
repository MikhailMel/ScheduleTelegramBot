package ru.scratty.newbot

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import ru.scratty.BotConfig
import ru.scratty.mongo.DBService
import ru.scratty.newbot.commands.*
import ru.scratty.newbot.events.EditGroupEvent
import ru.scratty.newbot.extensions.BaseTelegramBot
import ru.scratty.newbot.extensions.sendMessage

class ScheduleBot: BaseTelegramBot(BotConfig.BOT_USERNAME, BotConfig.BOT_TOKEN) {

    init {
        val dbService = DBService.INSTANCE

        registerCommand(StartCommand(dbService))
        registerCommand(SelectGroupCommand(dbService))
        registerCommand(CreateGroupCommand(dbService))
        registerCommand(CallsScheduleCommand())
        registerCommand(ScheduleCommand(dbService))
        registerCommand(SettingsCommand(dbService))

        registerEvent(Regex("edit_group")) { bot, chatId ->
            EditGroupEvent(dbService, bot, dbService.getUser(chatId))
        }
    }

    override fun incorrectCommand(message: Message) {
        sendMessage(this, message.chatId, "Неизвестная команда", menuKeyboard())
    }

    override fun menuKeyboard(): ReplyKeyboard {
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