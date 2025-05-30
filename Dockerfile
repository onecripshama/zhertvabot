FROM openjdk:17-jdk-slim

WORKDIR /app

# копируем wrapper
COPY gradlew .
COPY gradle gradle

# копируем остальные файлы
COPY build.gradle.kts settings.gradle.kts ./
COPY src src

# даем права на исполнение
RUN chmod +x ./gradlew

# собираем проект (без загрузки JDK toolchain)
RUN ./gradlew build --no-daemon

# запускаем бота
CMD ["./gradlew", "run", "--no-daemon"]
