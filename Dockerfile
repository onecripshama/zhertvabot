# Используем официальный образ Gradle для сборки
FROM gradle:7.6.1-jdk17-alpine AS build
WORKDIR /app

# Копируем исходные коды и файлы сборки
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src ./src

# Собираем приложение
RUN gradle build --no-daemon

# Финальный образ
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Копируем собранный JAR из предыдущего этапа
COPY --from=build /app/build/libs/*.jar app.jar

# Устанавливаем переменные окружения
ENV TELEGRAM_BOT_TOKEN="7615955771:AAGdP34f9RgPOJEqj3ZSRT6aDWlQk7M2lh4"
ENV TZ=Europe/Moscow

# Запускаем приложение
CMD ["java", "-jar", "app.jar"]