package ru.yandex.practicum.arch.cinema.events.kafka

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import ru.yandex.practicum.arch.cinema.events.dto.Event

private val logger = LoggerFactory.getLogger(UserEventConsumer::class.java)

@Component
@ConditionalOnProperty(
    name = ["consumer.user.enabled"],
    havingValue = "true",
    matchIfMissing = true
)
class UserEventConsumer {

    @KafkaListener(
        topics = ["\${topics.user-events}"],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun consume(event: Event) {
        logger.info("""
            Сообщение из топика user-events прочитано
            ====== USER EVENT =======
            ID: ${event.id}
            Тип: ${event.type}
            Время: ${event.timestamp}
            Детали: ${event.payload}
            =========================
        """.trimIndent())
    }
}