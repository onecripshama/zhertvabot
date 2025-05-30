package bot

import java.io.File
import java.net.URLEncoder
import java.net.URL
import java.net.HttpURLConnection

object TtsDownloader {
    private const val BASE_URL = "https://translate.google.com/translate_tts"
    private const val TIMEOUT = 10000 // 10 секунд

    fun download(text: String, lang: String = "ru"): File {
        val safeText = text.take(200)

        val encoded = URLEncoder.encode(safeText, "UTF-8")
        val url = "${BASE_URL}?ie=UTF-8&tl=$lang&client=tw-ob&q=$encoded"

        val connection = URL(url).openConnection() as HttpURLConnection
        connection.apply {
            connectTimeout = TIMEOUT
            readTimeout = TIMEOUT
            requestMethod = "GET"
        }

        return File.createTempFile("tts_", ".mp3").apply {
            deleteOnExit()
            connection.inputStream.use { input ->
                this.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }
}