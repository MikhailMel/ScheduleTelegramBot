package ru.scratty

import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.meta.TelegramBotsApi
import ru.scratty.bot.ScheduleBot

object BotLauncher {

    @JvmStatic
    fun main(args: Array<String>) {
        ApiContextInitializer.init()
        val telegramBotsApi = TelegramBotsApi()

        telegramBotsApi.registerBot(ScheduleBot())
    }

}