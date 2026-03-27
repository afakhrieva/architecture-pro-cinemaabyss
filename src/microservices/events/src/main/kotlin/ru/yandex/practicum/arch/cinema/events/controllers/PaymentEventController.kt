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
import ru.yandex.practicum.arch.cinema.events.dto.PaymentEventRequest
import ru.yandex.practicum.arch.cinema.events.kafka.EventProducerService
import java.time.OffsetDateTime
import java.util.UUID

private val logger = LoggerFactory.getLogger(PaymentEventController::class.java)

@RestController
@RequestMapping("/api/events")
class PaymentEventController(
    private val eventProducerService: EventProducerService
) {

    @PostMapping("/payment")
    fun createPaymentEvent(@RequestBody request: PaymentEventRequest): ResponseEntity<Any> {
        logger.info("Получен запрос на создание события платежа: payment_id=${request.paymentId}, status=${request.status}")

        try {
            // Валидация required полей
            if (request.paymentId <= 0) {
                return ResponseEntity.badRequest().body(Error("payment_id должен быть положительным числом"))
            }

            if (request.userId <= 0) {
                return ResponseEntity.badRequest().body(Error("user_id должен быть положительным числом"))
            }

            if (request.amount <= 0) {
                return ResponseEntity.badRequest().body(Error("amount должен быть положительным числом"))
            }

            if (request.status.isBlank()) {
                return ResponseEntity.badRequest().body(Error("status не может быть пустым"))
            }

            // Создаем payload для Kafka
            val payload = mutableMapOf<String, Any>(
                "payment_id" to request.paymentId,
                "user_id" to request.userId,
                "amount" to request.amount,
                "status" to request.status,
                "timestamp" to request.timestamp.toString()
            )

            // Добавляем опциональные поля
            request.methodType?.let { payload["method_type"] = it }

            // Создаем событие
            val event = Event(
                id = UUID.randomUUID().toString(),
                type = "payment",
                timestamp = OffsetDateTime.now(),
                payload = payload
            )

            val result = eventProducerService.sendPaymentEvent(event)
            // logger.info("Отправка события в Kafka (payments.events): $event")

            val response = EventResponse(
                status = "success",
                partition = result?.recordMetadata?.partition() ?: 0,
                offset = result?.recordMetadata?.offset() ?: 0,
                event = event
            )

            logger.info("Событие платежа создано: eventId=${event.id}")

            return ResponseEntity.status(HttpStatus.CREATED).body(response)

        } catch (e: Exception) {
            logger.error("Ошибка при создании события платежа", e)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Error("Internal Server Error"))
        }
    }
}