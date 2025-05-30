# Используем официальный образ OpenJDK 17
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем все файлы проекта в контейнер
COPY . .

# Даем права на запуск gradlew
RUN chmod +x ./gradlew

# Собираем проект
RUN ./gradlew build --no-daemon

# Запускаем бота
CMD ["./gradlew", "run"]
