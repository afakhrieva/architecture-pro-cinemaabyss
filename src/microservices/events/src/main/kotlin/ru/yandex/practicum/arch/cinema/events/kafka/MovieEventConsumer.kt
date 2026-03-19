package ru.yandex.practicum.arch.cinema.events.service

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import ru.yandex.practicum.arch.cinema.events.dto.Event

private val logger = LoggerFactory.getLogger(MovieEventConsumer::class.java)

@Component
@ConditionalOnProperty(
    name = ["consumer.movie.enabled"],
    havingValue = "true",
    matchIfMissing = true  // Если свойство не задано, считаем что включено
)
class MovieEventConsumer {

    @KafkaListener(
        topics = ["\${topics.movie-events}"],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun consume(event: Event) {
        logger.info("""
            Сообщение из топика movie-events прочитано
            ====== MOVIE EVENT ======
            ID: ${event.id}
            Тип: ${event.type}
            Время: ${event.timestamp}
            Детали: ${event.payload}
            =========================
        """.trimIndent())
    }
}