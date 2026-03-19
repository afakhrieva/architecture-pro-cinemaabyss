package ru.yandex.practicum.arch.cinema.events.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaConfig {

    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Value("\${topics.movie-events}")
    private lateinit var movieEventsTopic: String

    @Value("\${topics.user-events}")
    private lateinit var userEventsTopic: String

    @Value("\${topics.payment-events}")
    private lateinit var paymentEventsTopic: String

    @Bean
    fun movieEventsTopic(): NewTopic = NewTopic(movieEventsTopic, 3, 1)

    @Bean
    fun userEventsTopic(): NewTopic = NewTopic(userEventsTopic, 3, 1)

    @Bean
    fun paymentEventsTopic(): NewTopic = NewTopic(paymentEventsTopic, 3, 1)
}