plugins {
    kotlin("jvm") version "1.8.20"
    application
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.0.7")
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // Для улучшенных HTTP-запросов
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3") // Для асинхронности
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}