package ru.yandex.practicum.arch.cinema.events.storage.dao

import com.fasterxml.jackson.databind.JsonNode
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime

// ----- Таблица для событий фильмов -----
@Entity
@Table(name = "movie_events", indexes = [
    Index(name = "idx_movie_events_event_id", columnList = "event_id"),
    Index(name = "idx_movie_events_timestamp", columnList = "timestamp"),
    Index(name = "idx_movie_events_movie_id", columnList = "movie_id"),
    Index(name = "idx_movie_events_user_id", columnList = "user_id")
])
data class MovieEventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,

    @Column(name = "event_id", nullable = false)
    val eventId: String = "",

    @Column(name = "timestamp", nullable = false)
    val timestamp: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "movie_id")
    val movieId: Long? = null,

    @Column(name = "title")
    val title: String? = null,

    @Column(name = "action", length = 50)
    val action: String? = null,

    @Column(name = "user_id")
    val userId: Long? = null,

    @Column(name = "rating")
    val rating: Double? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb")
    val payload: JsonNode? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now()
) {
    // Конструктор без параметров для JPA
    constructor() : this(0, "", OffsetDateTime.now())
}