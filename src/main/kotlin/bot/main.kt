package bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message

fun main() {
    val token = System.getenv("TELEGRAM_BOT_TOKEN") ?: error("ERROR: TELEGRAM_BOT_TOKEN not set")

    val bot: Bot = bot {
        this.token = token
        dispatch {
            message {
                val text = message.text ?: return@message
                val chatId = ChatId.fromId(message.chat.id)

                try {
                    // Добавим проверку длины текста
                    if (text.length > 200) {
                        bot.sendMessage(chatId, "Текст слишком длинный (макс. 200 символов). Ваш текст: ${text.length}")
                        return@message
                    }

                    val voiceFile = TtsDownloader.download(text)
                    bot.sendVoice(
                        chatId = chatId,
                        audio = voiceFile
                    )
                    voiceFile.delete() // Удаляем временный файл после отправки
                } catch (e: Exception) {
                    e.printStackTrace() // Логируем ошибку
                    bot.sendMessage(chatId, "Ошибка синтеза: ${e.message}")
                }
            }
        }
    }
    bot.startPolling()
}