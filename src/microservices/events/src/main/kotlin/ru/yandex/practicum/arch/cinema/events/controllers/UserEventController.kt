package ru.yandex.practicum.arch.cinema.events.controllers.dto

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.yandex.practicum.arch.cinema.events.dto.Error
import ru.yandex.practicum.arch.cinema.events.dto.Event
import ru.yandex.practicum.arch.cinema.events.dto.EventResponse
import ru.yandex.practicum.arch.cinema.events.dto.UserEventRequest
import ru.yandex.practicum.arch.cinema.events.kafka.EventProducerService
import java.time.OffsetDateTime
import java.util.UUID

private val logger = LoggerFactory.getLogger(UserEventController::class.java)

@RestController
@RequestMapping("/api/events")
class UserEventController(
    private val eventProducerService: EventProducerService
) {

    @PostMapping("/user")
    fun createUserEvent(@RequestBody request: UserEventRequest): ResponseEntity<Any> {
        logger.info("Получен запрос на создание события пользователя: user_id=${request.userId}, action=${request.action}")

        try {
            // Валидация required полей
            if (request.userId <= 0) {
                return ResponseEntity.badRequest().body(Error("user_id должен быть положительным числом"))
            }

            if (request.action.isBlank()) {
                return ResponseEntity.badRequest().body(Error("action не может быть пустым"))
            }

            // Создаем payload для Kafka
            val payload = mutableMapOf<String, Any>(
                "user_id" to request.userId,
                "action" to request.action,
                "timestamp" to request.timestamp.toString()
            )

            // Добавляем опциональные поля
            request.username?.let { payload["username"] = it }
            request.email?.let { payload["email"] = it }

            // Создаем событие
            val event = Event(
                id = UUID.randomUUID().toString(),
                type = "user",
                timestamp = OffsetDateTime.now(),
                payload = payload
            )

            val result = eventProducerService.sendUserEvent(event)
            // logger.info("Отправка события в Kafka (users.events): $event")

            val response = EventResponse(
                status = "success",
                partition = result?.recordMetadata?.partition() ?: 0,
                offset = result?.recordMetadata?.offset() ?: 0,
                event = event
            )

            logger.info("Событие пользователя создано: eventId=${event.id}")

            return ResponseEntity.status(HttpStatus.CREATED).body(response)

        } catch (e: Exception) {
            logger.error("Ошибка при создании события пользователя", e)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Error("Internal Server Error"))
        }
    }
}