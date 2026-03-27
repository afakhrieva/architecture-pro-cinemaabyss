package ru.yandex.practicum.arch.cinema.events.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class UserEventRequest(
    @field:JsonProperty("user_id")
    val userId: Long,

    val username: String? = null,

    val email: String? = null,

    val action: String,

    val timestamp: OffsetDateTime = OffsetDateTime.now()
)