package ru.yandex.practicum.arch.cinema.events.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class MovieEventRequest(
    @field:JsonProperty("movie_id")
    val movieId: Long,

    val title: String,

    val action: String,

    @field:JsonProperty("user_id")
    val userId: Long? = null,

    val rating: Double? = null,

    val genres: List<String>? = null,

    val description: String? = null
)