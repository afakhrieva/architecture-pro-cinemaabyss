package ru.yandex.practicum.arch.cinema.events.controllers

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.yandex.practicum.arch.cinema.events.dto.Event
import ru.yandex.practicum.arch.cinema.events.dto.EventResponse
import ru.yandex.practicum.arch.cinema.events.dto.MovieEventRequest
import ru.yandex.practicum.arch.cinema.events.kafka.EventProducerService
import java.time.OffsetDateTime
import java.util.UUID

private val logger = LoggerFactory.getLogger(MovieEventController::class.java)

@RestController
@RequestMapping("/api/events")
class MovieEventController(
    private val eventProducerService: EventProducerService
) {

    @PostMapping("/movie")
    fun createMovieEvent(@RequestBody request: MovieEventRequest): ResponseEntity<Any> {
        logger.info("Получен запрос на создание события фильма: movie_id=${request.movieId}, action=${request.action}")

        try {
            // Валидация required полей
            if (request.movieId <= 0) {
                return ResponseEntity.badRequest().body(Error("movie_id должен быть положительным числом"))
            }

            if (request.title.isBlank()) {
                return ResponseEntity.badRequest().body(Error("title не может быть пустым"))
            }

            if (request.action.isBlank()) {
                return ResponseEntity.badRequest().body(Error("action не может быть пустым"))
            }

            // Создаем payload для Kafka
            val payload = mutableMapOf<String, Any>(
                "movie_id" to request.movieId,
                "title" to request.title,
                "action" to request.action
            )

            // Добавляем опциональные поля
            request.userId?.let { payload["user_id"] = it }
            request.rating?.let { payload["rating"] = it }
            request.genres?.let { payload["genres"] = it }
            request.description?.let { payload["description"] = it }

            // Создаем событие
            val event = Event(
                id = UUID.randomUUID().toString(),
                type = "movie",
                timestamp = OffsetDateTime.now(),
                payload = payload
            )

            val result = eventProducerService.sendMovieEvent(event)
            // logger.info("Отправка события в Kafka (movies.events): $event")

            // Формируем ответ
            val response = EventResponse(
                status = "success",
                partition = result?.recordMetadata?.partition() ?: 0,
                offset = result?.recordMetadata?.offset() ?: 0,
                event = event
            )

            logger.info("Событие фильма создано: eventId=${event.id}")

            return ResponseEntity.status(HttpStatus.CREATED).body(response)

        } catch (e: Exception) {
            logger.error("Ошибка при создании события фильма", e)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Error("Internal Server Error"))
        }
    }
}