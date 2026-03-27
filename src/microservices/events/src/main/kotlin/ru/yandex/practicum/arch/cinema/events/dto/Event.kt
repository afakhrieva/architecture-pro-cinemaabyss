package ru.yandex.practicum.arch.cinema.events.dto

import java.time.OffsetDateTime

data class Event(
    val id: String,
    val type: String,
    val timestamp: OffsetDateTime,
    val payload: Map<String, Any>
)