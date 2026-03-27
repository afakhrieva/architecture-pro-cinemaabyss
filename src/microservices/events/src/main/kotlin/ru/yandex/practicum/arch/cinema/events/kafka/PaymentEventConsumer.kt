package ru.yandex.practicum.arch.cinema.events.kafka

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import ru.yandex.practicum.arch.cinema.events.dto.Event

private val logger = LoggerFactory.getLogger(PaymentEventConsumer::class.java)

@Component
@ConditionalOnProperty(
    name = ["consumer.payment.enabled"],
    havingValue = "true",
    matchIfMissing = true
)
class PaymentEventConsumer {

    @KafkaListener(
        topics = ["\${topics.payment-events}"],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun consume(event: Event) {
        logger.info("""
            Сообщение из топика payment-events прочитано
            ====== PAYMENT EVENT ======
            ID: ${event.id}
            Тип: ${event.type}
            Время: ${event.timestamp}
            Детали: ${event.payload}
            ===========================
        """.trimIndent())
    }
}