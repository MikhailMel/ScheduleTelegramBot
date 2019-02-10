package ru.scratty.newbot.commands

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.scratty.newbot.extensions.Command
import ru.scratty.newbot.extensions.ISimpleCommand
import ru.scratty.newbot.extensions.sendMessage

class CallsScheduleCommand: Command(Regex("звонки|звонок")), ISimpleCommand {

    override fun handleMessage(sender: AbsSender, message: Message) {
        val text = "1 пара – 9:00-10:30\n" +
                "2 пара – 10:40-12:10\n" +
                "3 пара – 13:00-14:30\n" +
                "4 пара – 14:40-16:10\n" +
                "5 пара – 16:20-17:50\n" +
                "6 пара – 18:00-19:30"

        sendMessage(sender, message.chatId, text)
    }
}