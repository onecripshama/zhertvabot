# Этап 1: Сборка приложения с Gradle
FROM gradle:8.1.1-jdk17 AS builder

WORKDIR /app

# Кэшируем зависимости
COPY build.gradle.kts settings.gradle.kts ./
RUN gradle dependencies --no-daemon || true

# Копируем остальной проект и собираем
COPY . .
RUN gradle clean build --no-daemon --no-parallel

# Этап 2: Минимальный образ для запуска
FROM openjdk:17-jdk-slim

WORKDIR /app

# Копируем jar-файл из стадии сборки
COPY --from=builder /app/build/libs/*.jar app.jar

ENV TELEGRAM_BOT_TOKEN=${TELEGRAM_BOT_TOKEN}

# Точка входа
ENTRYPOINT ["java", "-jar", "app.jar"]
