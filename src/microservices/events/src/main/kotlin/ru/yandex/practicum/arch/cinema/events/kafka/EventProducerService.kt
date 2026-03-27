package ru.yandex.practicum.arch.cinema.events.kafka

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import ru.yandex.practicum.arch.cinema.events.dto.Event
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

private val logger = LoggerFactory.getLogger(EventProducerService::class.java)

@Service
class EventProducerService(
    private val kafkaTemplate: KafkaTemplate<String, Event>,
    @Value("\${topics.movie-events}") private val movieEventsTopic: String,
    @Value("\${topics.user-events}") private val userEventsTopic: String,
    @Value("\${topics.payment-events}") private val paymentEventsTopic: String
) {

    /**
     * Отправляет событие фильма в Kafka
     * @return CompletableFuture с результатом отправки
     */
    fun sendMovieEvent(event: Event): SendResult<String, Event>? {
        logger.debug("Отправка события фильма в топик $movieEventsTopic: ${event.id}")
        return sendEventSync(movieEventsTopic, event)
    }

    /**
     * Отправляет событие пользователя в Kafka
     */
    fun sendUserEvent(event: Event): SendResult<String, Event>? {
        logger.debug("Отправка события пользователя в топик $userEventsTopic: ${event.id}")
        return sendEventSync(userEventsTopic, event)
    }

    /**
     * Отправляет событие платежа в Kafka
     */
    fun sendPaymentEvent(event: Event):SendResult<String, Event>? {
        logger.debug("Отправка события платежа в топик $paymentEventsTopic: ${event.id}")
        return sendEventSync(paymentEventsTopic, event)
    }

    /**
     * Базовый метод отправки события в Kafka
     */
    private fun sendEvent(topic: String, event: Event): CompletableFuture<SendResult<String, Event>> {
        val future = kafkaTemplate.send(topic, event.id, event)

        future.whenComplete { result, ex ->
            when {
                ex != null -> {
                    logger.error("Ошибка отправки события в топик $topic: ${event.id}", ex)
                }
                else -> {
                    logger.info("Событие ${event.id} отправлено в топик $topic: " +
                        "partition=${result.recordMetadata.partition()}, " +
                        "offset=${result.recordMetadata.offset()}")
                }
            }
        }

        return future
    }

    /**
     * Синхронная отправка с получением результата
     */
    fun sendEventSync(topic: String, event: Event): SendResult<String, Event>? {
        return try {
            sendEvent(topic, event).get(5, TimeUnit.SECONDS)
        } catch (e: Exception) {
            logger.error("Ошибка синхронной отправки события в топик $topic", e)
            null
        }
    }
}