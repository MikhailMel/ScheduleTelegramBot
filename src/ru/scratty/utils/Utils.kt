package ru.scratty.utils

import ru.scratty.mongo.models.Lesson
import java.util.*


fun List<Lesson>.filterByCalendar(calendar: Calendar) = this.filter { lesson ->
    if (calendar.get(Calendar.DAY_OF_WEEK) == lesson.dayNumber) {
        if (lesson.weeks.size == 0) {
            (calendar.get(Calendar.WEEK_OF_YEAR) - Config.WEEKS_SHIFT) % 2 == lesson.typeWeek % 2
        } else {
            lesson.weeks.any { (calendar.get(Calendar.WEEK_OF_YEAR) - Config.WEEKS_SHIFT) == it }
        }
    } else {
        false
    }
}

fun List<Lesson>.filterByCalendarAndLessonNumber(calendar: Calendar, number: Int) = this.filter { lesson ->
    if (calendar.get(Calendar.DAY_OF_WEEK) == lesson.dayNumber && lesson.number == number) {
        if (lesson.weeks.size == 0) {
            (calendar.get(Calendar.WEEK_OF_YEAR) - Config.WEEKS_SHIFT) % 2 == lesson.typeWeek % 2
        } else {
            lesson.weeks.any { (calendar.get(Calendar.WEEK_OF_YEAR) - Config.WEEKS_SHIFT) == it }
        }
    } else {
        false
    }
}