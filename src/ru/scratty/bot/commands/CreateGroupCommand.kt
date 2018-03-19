package ru.scratty.bot.commands

import org.telegram.telegrambots.api.objects.Update
import ru.scratty.db.Group
import ru.scratty.db.User

class CreateGroupCommand: Command("/создать|/create".toRegex()) {

    override fun handleCommand(update: Update, user: User): String {
        val words = update.message.text.split(" ")

        return if (words.size == 2) {
            db.addGroup(Group(db.countGroups() + 1, user._id, words[1]))

            user.groupId = db.countGroups()
            db.updateUser(user)

            "Группа ${words[1]} успешно создана"
        } else {
            "Некорректное кол-во аргументов"
        }
    }
}