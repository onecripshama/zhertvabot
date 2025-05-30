package bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton

fun main() {
    val token = System.getenv("TELEGRAM_BOT_TOKEN") ?: error("ERROR: TELEGRAM_BOT_TOKEN not set")

    val bot: Bot = bot {
        this.token = token
        dispatch {
            message {
                val text = message.text ?: return@message
                val chatId = ChatId.fromId(message.chat.id)

                try {
                    if (text.length > 200) {
                        bot.sendSticker(
                            chatId = chatId,
                            sticker = "CAACAgIAAxkBAAEOgjFoKETTxDt5litsH9rLbdLQTYSI8wACDHMAAoJeKUmf_ZuXfNoHIjYE",
                            replyMarkup = InlineKeyboardMarkup.create(
                                listOf(
                                    InlineKeyboardButton.CallbackData(
                                        text = "ОКАК",
                                        callbackData = "repeat_sticker"
                                    )
                                )
                            )
                        )
                        return@message
                    }

                    val voiceFile = TtsDownloader.download(text)
                    bot.sendVoice(
                        chatId = chatId,
                        audio = voiceFile
                    )
                    voiceFile.delete()
                } catch (e: Exception) {
                    e.printStackTrace()
                    bot.sendMessage(chatId, "Ошибка синтеза: ${e.message}")
                }
            }
        }
    }
    bot.startPolling()
}