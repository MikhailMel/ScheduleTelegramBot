package ru.scratty.utils

import ru.scratty.bot.notifications.Time

object Config {

    const val WEEKS_SHIFT = 5

    val LESSONS = arrayListOf(Time(9 - 3, 30), Time(10 - 3, 30),
            Time(12 - 3, 10), Time(14 - 3, 30), Time(16 - 3, 10),
            Time(17 - 3, 50))

}