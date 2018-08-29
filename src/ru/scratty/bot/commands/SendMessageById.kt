package ru.scratty.bot.commands

import org.telegram.telegrambots.meta.api.objects.Update
import ru.scratty.db.User

class SendMessageById: Command("/msg".toRegex()) {

    private var id: Long = 0
    private var textForSend = ""

    override fun handleCommand(update: Update, user: User): String {
        isSendMessage = true

        val args = text.split(" ")
        return if (args.size >= 3) {
            if (args[0] == "/msg" && args[1].toLongOrNull() != null) {
                id = args[1].toLong()
                textForSend = text.split(args[1])[1].trim()

                "Ваше сообщение успешно отправлено"
            } else {
                "/msg <id собеседника> <сообщение>"
            }
        } else {
            "Неверное кол-во аргументов\n/msg <id собеседника> <сообщение>"
        }
    }

    override fun getMessageForSendPair(): Pair<Long, String> {
        return Pair(id, textForSend)
    }
}