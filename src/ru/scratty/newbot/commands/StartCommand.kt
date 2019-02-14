package ru.scratty.newbot.commands

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.scratty.mongo.DBService
import ru.scratty.mongo.models.User
import ru.scratty.newbot.extensions.BaseTelegramBot
import ru.scratty.newbot.extensions.Command
import ru.scratty.newbot.extensions.ISimpleCommand
import ru.scratty.newbot.extensions.sendMessage

class StartCommand(private val dbService: DBService): Command(Regex("start")), ISimpleCommand {

    override fun handleMessage(sender: AbsSender, message: Message) {
        if (!dbService.userIsExists(message.chatId)) {
            val user = User(message.chatId, message.chat.firstName + " " + message.chat.lastName, message.chat.userName)
            dbService.addUser(user)

            val keyboard = SelectGroupCommand(dbService).getKeyboard()

            sendMessage(sender, message.chatId, "Добро пожаловать!", (sender as BaseTelegramBot).menuKeyboard())
            sendMessage(sender, message.chatId, "Для начала работы с ботом выберите группу", keyboard)
        } else {
            sendMessage(sender, message.chatId, "Вы уже зарегестрированы:)", (sender as BaseTelegramBot).menuKeyboard())
        }
    }
}