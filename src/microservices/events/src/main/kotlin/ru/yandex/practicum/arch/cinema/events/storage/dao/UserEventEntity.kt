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

// ----- Таблица для событий пользователей -----
@Entity
@Table(name = "user_events", indexes = [
    Index(name = "idx_user_events_event_id", columnList = "event_id"),
    Index(name = "idx_user_events_timestamp", columnList = "timestamp"),
    Index(name = "idx_user_events_user_id", columnList = "user_id")
])
data class UserEventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,

    @Column(name = "event_id", nullable = false)
    val eventId: String = "",

    @Column(name = "timestamp", nullable = false)
    val timestamp: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "user_id")
    val userId: Long? = null,

    @Column(name = "action", length = 50)
    val action: String? = null,

    @Column(name = "username")
    val username: String? = null,

    @Column(name = "email")
    val email: String? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb")
    val payload: JsonNode? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now()
) {
    constructor() : this(0, "", OffsetDateTime.now())
}