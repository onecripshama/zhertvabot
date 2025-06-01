package bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.sun.net.httpserver.HttpServer
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.URL
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.thread

fun main() {
    val token = System.getenv("TELEGRAM_BOT_TOKEN") ?: error("ERROR: TELEGRAM_BOT_TOKEN not set")
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080

    // Фейковый HTTP-сервер потому что я бомжара и у меня нет денег на оплату background server
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

    // Self-ping каждую 10-ю минуту по той же причине, что и выше
    Timer().scheduleAtFixedRate(object : TimerTask() {
        override fun run() {
            try {
                val url = URL("http://localhost:$port/")
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"
                    connectTimeout = 5000
                    readTimeout = 5000
                    inputStream.bufferedReader().use { it.readText() }
                    println("Self-ping successful")
                }
            } catch (e: Exception) {
                println("Self-ping failed: ${e.message}")
            }
        }
    }, 0, 10 * 60 * 1000)

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