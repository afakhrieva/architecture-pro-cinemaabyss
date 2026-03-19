FROM eclipse-temurin:21-jdk-alpine

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

# Копируем jar
RUN cp build/libs/*.jar app.jar

# Порт
EXPOSE 8080

# Запуск
CMD ["java", "-jar", "app.jar"]