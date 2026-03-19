package ru.yandex.practicum.arch.cinema.events.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class PaymentEventRequest(
    @field:JsonProperty("payment_id")
    val paymentId: Long,

    @field:JsonProperty("user_id")
    val userId: Long,

    val amount: Double,

    val status: String,

    val timestamp: OffsetDateTime = OffsetDateTime.now(),

    @field:JsonProperty("method_type")
    val methodType: String? = null
)