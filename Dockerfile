# Используем официальный OpenJDK 17 (легковесный slim образ)
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию в контейнере
WORKDIR /app

# Копируем файлы gradle wrapper и настройки сначала (для кэширования)
COPY gradlew .
COPY gradle gradle

# Копируем файлы проекта
COPY build.gradle.kts settings.gradle.kts ./
COPY src src

# Делаем gradlew исполняемым
RUN chmod +x gradlew

# Запускаем сборку проекта (без запуска демона Gradle)
RUN ./gradlew build --no-daemon

# Команда запуска бота
CMD ["./gradlew", "run"]
