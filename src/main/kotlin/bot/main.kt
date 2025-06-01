package bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import kotlin.concurrent.thread

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

    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080

    // фейк сервер в отдельном потоке потому что я бомжара и у меня нет денег на подписку background worker
    thread {
        val server = HttpServer.create(InetSocketAddress(port), 0)
        server.createContext("/") { exchange ->
            val response = "Bot is running"
            exchange.sendResponseHeaders(200, response.length.toLong())
            exchange.responseBody.use { it.write(response.toByteArray()) }
        }
        server.executor = null
        server.start()
        println("Fake HTTP server started on port $port")
    }

    bot.startPolling()
}