package ru.scratty.newbot.events

import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import ru.scratty.mongo.DBService
import ru.scratty.mongo.models.Lesson
import ru.scratty.mongo.models.User
import ru.scratty.newbot.extensions.BaseEvent
import ru.scratty.newbot.extensions.BaseTelegramBot
import ru.scratty.newbot.extensions.sendMessage
import ru.scratty.newbot.extensions.updateMessage
import ru.scratty.utils.getDayName
import java.util.*


//TODO: переписать все
class EditGroupEvent(private val dbService: DBService,
                     private val bot: BaseTelegramBot,
                     private val user: User): BaseEvent {

    private lateinit var lesson: Lesson

    private var step = 0

    override fun checkExit(message: Message) = checkExit(message.text)

    override fun checkExit(callbackQuery: CallbackQuery) = checkExit(callbackQuery.data)

    override fun handleMessage(message: Message) {
        when(step) {
            1 -> secondStep(message.text.trim())
            3 -> fourthStep(message.text.trim())
            5 -> sixthStep(message.text.trim())
        }
    }

    override fun handleCallbackQuery(callbackQuery: CallbackQuery) {
       when(step) {
           0 -> firstStep(callbackQuery)
           1 -> secondStepCallback(callbackQuery)
           2 -> thirdStepCallback(callbackQuery)
           3 -> fourthStepCallback(callbackQuery)
           4 -> fifthStepCallback(callbackQuery)
           6 -> seventhStepCallback(callbackQuery)
           7 -> eighthStepCallback(callbackQuery)
           8 -> ninthStepCallback(callbackQuery)
       }
    }

    private fun checkExit(data: String): Boolean {
        return if (Regex("выход|exit").containsMatchIn(data.toLowerCase().trim())) {
            sendMessage("Редактирование завершено")
            true
        } else {
            false
        }
    }



    private fun firstStep(callbackQuery: CallbackQuery) {
        lesson = Lesson()
        step = 1

        val group = dbService.getGroup(user.groupId)

        val lessons = ArrayList<Lesson>()
        for(lesson in dbService.getLessons(group.lessons).reversed()) {
            if (lessons.filter { it.name == lesson.name }.count() == 0) {
                lessons.add(lesson)
            }
            if (lessons.size >= 5) {
                break
            }
        }

        sendMessage("Редактирование предметов запущено", exitKeyboard())
        sendMessage("Введите название предмета или выберите его ниже", lessonsNamesKeyboard(lessons))
    }

    private fun secondStep(name: String) {
        if (name.isEmpty()) {
            sendMessage("Название предмета не может быть пустым")
            return
        }

        step = 2
        lesson.name = name

        sendMessage("Выберите день недели:", daysOfWeekKeyboard())
    }

    private fun secondStepCallback(callbackQuery: CallbackQuery) {
        if (!callbackQuery.data.contains("lesson_")) {
            return
        }

        val selectedLessonId = callbackQuery.data.replace("lesson_", "")

        step = 2
        lesson.name = dbService.getLesson(selectedLessonId).name

        updateMessage(callbackQuery.message, "Выберите день недели:", daysOfWeekKeyboard())
    }

    private fun thirdStepCallback(callbackQuery: CallbackQuery) {
        if (!callbackQuery.data.contains("day_")) {
            return
        }

        step = 3
        lesson.dayNumber = callbackQuery.data.replace("day_", "").toInt()


        updateMessage(callbackQuery.message, "Выберите тип недели или напишите недели через запятую:", weekTypeKeyboard())
    }

    private fun fourthStep(weeks: String) {
        if (weeks.isEmpty()) {
            sendMessage("Введите недели через запятую")
            return
        }

        step = 4
        weeks.split(",").forEach {
            lesson.weeks.add(it.trim().toInt())
        }

        sendMessage("Выберите номер пары:", lessonNumberKeyboard())
    }

    private fun fourthStepCallback(callbackQuery: CallbackQuery) {
        if (!callbackQuery.data.contains("week_type_")) {
            return
        }

        step = 4
        lesson.typeWeek = callbackQuery.data.replace("week_type_", "").toInt()

        updateMessage(callbackQuery.message, "Выберите номер пары:", lessonNumberKeyboard())
    }

    private fun fifthStepCallback(callbackQuery: CallbackQuery) {
        if (!callbackQuery.data.contains("lesson_number_")) {
            return
        }

        step = 5
        lesson.number = callbackQuery.data.replace("lesson_number_", "").toInt()

        updateMessage(callbackQuery.message, "Введите номер аудитории")
    }

    private fun sixthStep(audience: String) {
        if (audience.isEmpty()) {
            sendMessage("Введите аудиторию")
            return
        }

        step = 6
        lesson.audience = audience

        sendMessage("Выберите тип занятия:", lessonTypeKeyboard())
    }

    private fun seventhStepCallback(callbackQuery: CallbackQuery) {
        if (!callbackQuery.data.contains("lesson_type_")) {
            return
        }

        step = 7
        lesson.type = callbackQuery.data.replace("lesson_type_", "")

        updateMessage(callbackQuery.message, checkLessonString(), saveKeyboard())
    }

    private fun eighthStepCallback(callbackQuery: CallbackQuery) {
        if (!callbackQuery.data.contains("save_")) {
            return
        }

        step = 8
        val save = callbackQuery.data.replace("save_", "")
        if (save == "yes") {
            val group = dbService.getGroup(user.groupId)

            val id = dbService.addLesson(lesson)
            group.lessons.add(id)

            dbService.editGroup(group)

            updateMessage(callbackQuery.message, "Занятие успешно добавлено. \nДобавить еще одно?", endKeyboard())
        } else {
            firstStep(callbackQuery)
        }
    }

    private fun ninthStepCallback(callbackQuery: CallbackQuery) {
        if (!callbackQuery.data.contains("end_")) {
            return
        }

        val state = callbackQuery.data.replace("end_", "")
        if (state == "yes") {
            firstStep(callbackQuery)
        } else {
            bot.stopEvent(user)
        }
    }

    private fun checkLessonString() = StringBuilder().apply {
        appendln("Проверьте введенные данные:")
        appendln("Название: ${lesson.name}")
        appendln("День недели: ${getDayName(lesson.dayNumber)}")

        if (lesson.weeks.isEmpty()) {
            appendln("Тип недели: ${lesson.typeWeek}")
        } else {
            append("Недели:")
            lesson.weeks.forEach {
                append(" $it,")
            }
            deleteCharAt(length - 1)
            appendln()
        }

        appendln("Номер пары: ${lesson.number}")
        appendln("Аудитория: ${lesson.audience}")
        appendln("Тип: ${lesson.type}")
    }.toString()

    private fun exitKeyboard(): ReplyKeyboard {
        val keyboardMarkup = ReplyKeyboardMarkup()
        val keyboard = ArrayList<KeyboardRow>()

        val row = KeyboardRow()
        row.add("Выход")
        keyboard.add(row)

        keyboardMarkup.keyboard = keyboard
        keyboardMarkup.resizeKeyboard = true

        return keyboardMarkup
    }

    private fun lessonsNamesKeyboard(lessons: List<Lesson>): InlineKeyboardMarkup {
        val keyboardMarkup = InlineKeyboardMarkup()
        val rows = mutableListOf<MutableList<InlineKeyboardButton>>()

        lessons.forEach {
            rows.add(arrayListOf(InlineKeyboardButton(it.name.trim()).setCallbackData("lesson_${it.id}")))
        }

        keyboardMarkup.keyboard = rows
        return keyboardMarkup
    }

    private fun daysOfWeekKeyboard(): InlineKeyboardMarkup {
        val keyboardMarkup = InlineKeyboardMarkup()
        val rows = mutableListOf<MutableList<InlineKeyboardButton>>()

        for (i in 2 .. 7) {
            rows.add(arrayListOf(InlineKeyboardButton(getDayName(i)).setCallbackData("day_$i")))
        }

        keyboardMarkup.keyboard = rows
        return keyboardMarkup
    }

    private fun weekTypeKeyboard(): InlineKeyboardMarkup {
        val keyboardMarkup = InlineKeyboardMarkup()
        val rows = mutableListOf<MutableList<InlineKeyboardButton>>()
        val row = ArrayList<InlineKeyboardButton>()

        row.add(InlineKeyboardButton("I").setCallbackData("week_type_1"))
        row.add(InlineKeyboardButton("II").setCallbackData("week_type_2"))
        rows.add(row)

        keyboardMarkup.keyboard = rows
        return keyboardMarkup
    }

    private fun lessonNumberKeyboard(): InlineKeyboardMarkup {
        val keyboardMarkup = InlineKeyboardMarkup()
        val rows = mutableListOf<MutableList<InlineKeyboardButton>>()

        for (i in 1 .. 3) {
            val num = i * 2 - 1

            val row = ArrayList<InlineKeyboardButton>()
            row.add(InlineKeyboardButton("$num").setCallbackData("lesson_number_$num"))
            row.add(InlineKeyboardButton("${num + 1}").setCallbackData("lesson_number_${num + 1}"))

            rows.add(row)
        }

        keyboardMarkup.keyboard = rows
        return keyboardMarkup
    }

    private fun lessonTypeKeyboard(): InlineKeyboardMarkup {
        val keyboardMarkup = InlineKeyboardMarkup()
        val rows = mutableListOf<MutableList<InlineKeyboardButton>>()
        val row = ArrayList<InlineKeyboardButton>()

        row.add(InlineKeyboardButton("лек").setCallbackData("lesson_type_лек"))
        row.add(InlineKeyboardButton("пр").setCallbackData("lesson_type_пр"))
        row.add(InlineKeyboardButton("лаб").setCallbackData("lesson_type_лаб"))
        rows.add(row)

        keyboardMarkup.keyboard = rows
        return keyboardMarkup
    }

    private fun saveKeyboard(): InlineKeyboardMarkup {
        val keyboardMarkup = InlineKeyboardMarkup()
        val rows = mutableListOf<MutableList<InlineKeyboardButton>>()
        val row = ArrayList<InlineKeyboardButton>()

        row.add(InlineKeyboardButton("Сохранить").setCallbackData("save_yes"))
        row.add(InlineKeyboardButton("Начать сначала").setCallbackData("save_no"))
        rows.add(row)

        keyboardMarkup.keyboard = rows
        return keyboardMarkup
    }

    private fun endKeyboard(): InlineKeyboardMarkup {
        val keyboardMarkup = InlineKeyboardMarkup()
        val rows = mutableListOf<MutableList<InlineKeyboardButton>>()
        val row = ArrayList<InlineKeyboardButton>()

        row.add(InlineKeyboardButton("Да").setCallbackData("end_yes"))
        row.add(InlineKeyboardButton("Выход").setCallbackData("end_exit"))
        rows.add(row)

        keyboardMarkup.keyboard = rows
        return keyboardMarkup
    }

    private fun sendMessage(text: String, keyboard: ReplyKeyboard = InlineKeyboardMarkup()) {
        sendMessage(bot, user.id, text, keyboard)
    }

    private fun updateMessage(message: Message, text: String, keyboard: InlineKeyboardMarkup = InlineKeyboardMarkup()) {
        updateMessage(bot, message, text, keyboard)
    }
}