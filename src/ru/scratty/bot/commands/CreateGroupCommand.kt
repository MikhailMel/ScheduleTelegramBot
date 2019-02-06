package ru.scratty.bot.commands

import org.telegram.telegrambots.meta.api.objects.Update
import ru.scratty.mongo.models.Group
import ru.scratty.mongo.models.User

class CreateGroupCommand: Command("/создать|/create".toRegex()) {

    override fun handleCommand(update: Update, user: User): String {
        val words = update.message.text.split(" ")

        return if (words.size == 2) {
            val id = dbService.addGroup(Group(authorId = user.id, name = words[1]))

            user.groupId = id
            dbService.editUser(user)

            "Группа ${words[1]} успешно создана"
        } else {
            "Некорректное кол-во аргументов"
        }
    }
}