package ru.scratty.utils

import ru.scratty.bot.notifications.Time

object Config {

    const val WEEKS_SHIFT = 35

    val LESSONS = arrayListOf(Time(9 - 7, 30), Time(10 - 7, 30),
            Time(12 - 7, 10), Time(14 - 7, 30), Time(16 - 7, 10),
            Time(17 - 7, 50))

}