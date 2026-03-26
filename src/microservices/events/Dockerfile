FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Копируем файлы сборки
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
COPY gradlew ./

# Даем права на выполнение gradlew
RUN chmod +x gradlew

# Скачиваем зависимости
RUN ./gradlew dependencies --no-daemon

# Копируем исходный код
COPY src ./src

# Собираем приложение
RUN ./gradlew clean build --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Устанавливаем snappy
RUN apk add --no-cache snappy

COPY --from=builder /app/build/libs/app.jar app.jar

# Порт
EXPOSE 8082

# Запуск
CMD ["java", "-jar", "app.jar"]