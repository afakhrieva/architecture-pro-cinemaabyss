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

// ----- Таблица для событий платежей -----
@Entity
@Table(name = "payment_events", indexes = [
    Index(name = "idx_payment_events_event_id", columnList = "event_id"),
    Index(name = "idx_payment_events_timestamp", columnList = "timestamp"),
    Index(name = "idx_payment_events_user_id", columnList = "user_id"),
    Index(name = "idx_payment_events_payment_id", columnList = "payment_id")
])
data class PaymentEventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,

    @Column(name = "event_id", nullable = false)
    val eventId: String = "",

    @Column(name = "timestamp", nullable = false)
    val timestamp: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "payment_id")
    val paymentId: Long? = null,

    @Column(name = "user_id")
    val userId: Long? = null,

    @Column(name = "amount")
    val amount: Double? = null,

    @Column(name = "status", length = 50)
    val status: String? = null,

    @Column(name = "method_type", length = 50)
    val methodType: String? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb")
    val payload: JsonNode? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now()
) {
    constructor() : this(0, "", OffsetDateTime.now())
}