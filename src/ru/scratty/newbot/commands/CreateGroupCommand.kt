package ru.scratty.newbot.commands

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.scratty.mongo.DBService
import ru.scratty.mongo.models.Group
import ru.scratty.newbot.extensions.Command
import ru.scratty.newbot.extensions.ISimpleCommand
import ru.scratty.newbot.extensions.sendMessage

class CreateGroupCommand(private val dbService: DBService): Command(Regex("создать|create")), ISimpleCommand {

    override fun handleMessage(sender: AbsSender, message: Message) {
        val words = message.text.split(" ")

        if (words.size == 2) {
            val user = dbService.getUser(message.chatId)
            val id = dbService.addGroup(Group(authorId = user.id, name = words[1]))

            user.groupId = id
            dbService.editUser(user)

            sendMessage(sender, message.chatId, "Группа успешно создана")
        } else {
            sendMessage(sender, message.chatId, "Некорректное количество аргументов")
        }
    }
}