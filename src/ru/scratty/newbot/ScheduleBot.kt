package ru.scratty.newbot

import org.telegram.telegrambots.meta.api.objects.Message
import ru.scratty.BotConfig
import ru.scratty.mongo.DBService
import ru.scratty.newbot.commands.*
import ru.scratty.newbot.extensions.CommandsBot
import ru.scratty.newbot.extensions.sendMessage

class ScheduleBot: CommandsBot(BotConfig.BOT_USERNAME, BotConfig.BOT_TOKEN) {

    init {
        val dbService = DBService.INSTANCE

        registerCommand(StartCommand(dbService))
        registerCommand(SelectGroupCommand(dbService))
        registerCommand(CreateGroupCommand(dbService))
        registerCommand(CallsScheduleCommand())
        registerCommand(ScheduleCommand(dbService))
        registerCommand(SettingsCommand(dbService))
    }

    override fun incorrectCommand(message: Message) {
        sendMessage(this, message.chatId, "Неизвестная команда")
    }
}