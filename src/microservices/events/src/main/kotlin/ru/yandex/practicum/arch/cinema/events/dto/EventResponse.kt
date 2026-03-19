package ru.yandex.practicum.arch.cinema.events.dto

data class EventResponse(
    val status: String,
    val partition: Int,
    val offset: Long,
    val event: Event
)